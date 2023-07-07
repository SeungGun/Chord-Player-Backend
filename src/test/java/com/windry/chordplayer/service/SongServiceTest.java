package com.windry.chordplayer.service;

import com.windry.chordplayer.dto.song.DetailSongDto;
import com.windry.chordplayer.dto.song.FiltersOfDetailSong;
import com.windry.chordplayer.exception.ImpossibleConvertGenderException;
import com.windry.chordplayer.exception.NoSuchDataException;
import com.windry.chordplayer.spec.Gender;
import com.windry.chordplayer.domain.Genre;
import com.windry.chordplayer.domain.Song;
import com.windry.chordplayer.domain.SongGenre;
import com.windry.chordplayer.dto.song.CreateSongDto;
import com.windry.chordplayer.dto.lyrics.LyricsDto;
import com.windry.chordplayer.exception.DuplicateTitleAndArtistException;
import com.windry.chordplayer.repository.ChordsRepository;
import com.windry.chordplayer.repository.GenreRepository;
import com.windry.chordplayer.repository.lyrics.LyricsRepository;
import com.windry.chordplayer.repository.song.SongRepository;
import com.windry.chordplayer.spec.Tuning;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@SpringBootTest
@Transactional
class SongServiceTest {

    @Autowired
    private SongRepository songRepository;
    @Autowired
    private LyricsRepository lyricsRepository;
    @Autowired
    private ChordsRepository chordsRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private SongService songService;
    @Autowired
    private EntityManager entityManager;

    @DisplayName("노래 데이터를 생성할 때, 중복되는 제목과 가수가 있을 경우 예외를 발생한다.")
    @Test
    void duplicateSongTitleAndArtistException() {
        // given
        Genre genre = Genre.builder().name("락").build();
        genreRepository.save(genre);

        Song song = new Song();

        CreateSongDto songDto = CreateSongDto.builder()
                .title("하늘을 달리다")
                .artist("이적")
                .originalKey("E")
                .bpm(116)
                .gender(Gender.MALE)
                .modulation(null)
                .contents(null)
                .genres(null)
                .build();

        List<SongGenre> songGenres = new ArrayList<>();
        SongGenre songGenre = SongGenre.builder().genre(genre).song(song).build();
        songGenres.add(songGenre);

        song.changeRequestFields(
                songDto.getTitle(),
                songDto.getArtist(),
                songDto.getOriginalKey(),
                songDto.getGender(),
                songDto.getBpm(),
                songDto.getModulation(),
                null,
                songGenres);

        Song differentArtistSong = new Song();

        differentArtistSong.changeRequestFields(
                songDto.getTitle(),
                "허각",
                songDto.getOriginalKey(),
                songDto.getGender(),
                songDto.getBpm(),
                songDto.getModulation(),
                null,
                songGenres
        );

        // when
        Song song1 = songRepository.save(song);
        Song song2 = songRepository.save(differentArtistSong);

        // then
        Assertions.assertEquals("이적", songRepository.findById(song1.getId()).get().getArtist());
        Assertions.assertEquals("허각", songRepository.findById(song2.getId()).get().getArtist());
        Assertions.assertEquals(1, songRepository.findById(song2.getId()).get().getSongGenres().size());
        Assertions.assertThrows(DuplicateTitleAndArtistException.class, () -> songService.validateDupSongAndArtist("하늘을달리다", "이적"));
    }

    @DisplayName("4박자 마디의 가사와 코드가 포함된 새로운 노래 데이터를 생성한다.")
    @Test
    void createNewSong() {
        // given
        Genre genre = Genre.builder().name("락").build();
        genreRepository.save(genre);

        Song song = new Song();
        song.changeRequestFields(
                "하늘을 달리다", "이적", "E", Gender.MALE, 116, null, null, null
        );

        List<String> genreList = new ArrayList<>();
        genreList.add("락");

        CreateSongDto songDto = CreateSongDto.builder()
                .title("하늘을 달리다")
                .artist("이적")
                .originalKey("E")
                .bpm(116)
                .gender(Gender.MALE)
                .modulation(null)
                .contents(null)
                .genres(genreList)
                .build();

        List<LyricsDto> lyricsDtoList = new ArrayList<>();
        lyricsDtoList.add(LyricsDto.builder()
                .tag("INTRO")
                .lyrics(null)
                .chords(LyricsDto.getAllChords("B", "A"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag("INTRO")
                .lyrics(null)
                .chords(LyricsDto.getAllChords("E", "A"))
                .build());

        lyricsDtoList.add(LyricsDto.builder()
                .tag("INTRO")
                .lyrics(null)
                .chords(LyricsDto.getAllChords("B", "A"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("두근거렸지 난 결국")
                .chords(LyricsDto.getAllChords("E", "Aadd2"))
                .build());
        songDto.setContents(lyricsDtoList);

        // when
        Long newSong = songService.createNewSong(songDto);

        Song song1 = songRepository.findById(newSong).get();
        // then
        org.assertj.core.api.Assertions.assertThat(song)
                .usingRecursiveComparison()
                .ignoringFields("createdDate", "modifiedDate", "id", "lyricsList", "songGenres")
                .isEqualTo(song1);
    }

    @Test
    @DisplayName("상세 노래 정보에서 키 변경에 대한 여러가지 필터를 적용해본다.")
    void changeKeyOfDetailSong() {
        // given
        Genre genre = Genre.builder().name("발라드").build();
        genreRepository.save(genre);

        List<String> genreList = new ArrayList<>();
        genreList.add("발라드");

        CreateSongDto songDto = CreateSongDto.builder()
                .title("다정히 내 이름을 부르면")
                .artist("경서예지")
                .originalKey("Db")
                .bpm(72)
                .gender(Gender.MIXED)
                .modulation("Db-Eb-F-G")
                .contents(null)
                .genres(genreList)
                .build();

        List<LyricsDto> lyricsDtoList = new ArrayList<>();
        lyricsDtoList.add(LyricsDto.builder()
                .tag("INTRO")
                .lyrics(null)
                .chords(LyricsDto.getAllChords("Db", "Ab"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag("INTRO")
                .lyrics(null)
                .chords(LyricsDto.getAllChords("Bbm7", "Fm7"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag("INTRO")
                .lyrics(null)
                .chords(LyricsDto.getAllChords("Gb", "Gbm6"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("끝 없")
                .chords(LyricsDto.getAllChords("Db", "Dbsus4", "Db"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("이 별빛이 내리던 밤")
                .chords(LyricsDto.getAllChords("Db", "Ab"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("기분 좋은 바람이")
                .chords(LyricsDto.getAllChords("Bbm7", "Db/Ab"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("두 뺨을 스치고")
                .chords(LyricsDto.getAllChords("Gb", "Db/F"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("새벽 바다 한 곳을 보")
                .chords(LyricsDto.getAllChords("Ebm", "Gb/Ab", "Ab"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("는 아름다운 너와 나")
                .chords(LyricsDto.getAllChords("Db", "Ab"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("그림을 그려갔어")
                .chords(LyricsDto.getAllChords("Bbm", "Bbm7/Ab"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("모래 위 떨린 손")
                .chords(LyricsDto.getAllChords("Gbm", "Gbm6"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("끝으로 날 향")
                .chords(LyricsDto.getAllChords("Db", "Fm/C"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("해 웃어주는 입술")
                .chords(LyricsDto.getAllChords("Bbm", "BbmM7/A"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("사랑스러운 두눈을 가진 네")
                .chords(LyricsDto.getAllChords("Ebm", "Db/F", "Gb", "Eb7/G"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag("MODULATION")
                .lyrics("가 다정히 내 이름을")
                .chords(LyricsDto.getAllChords("Ab", "Bb"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("부르면 내 마음이")
                .chords(LyricsDto.getAllChords("Eb", "Bb"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("녹아내려 언제나")
                .chords(LyricsDto.getAllChords("Cm", "Cm7/Bb"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("나 하날 위해 준비된")
                .chords(LyricsDto.getAllChords("Ab", "Eb/G"))
                .build());

        songDto.setContents(lyricsDtoList);

        // when
        Long newSong = songService.createNewSong(songDto);

        FiltersOfDetailSong filters = FiltersOfDetailSong.builder()
                .key(null)
                .isKeyUp(null)
                .convertGender(null)
                .tuning(null)
                .capo(1)
                .build();

        FiltersOfDetailSong filters2 = FiltersOfDetailSong.builder()
                .key(3)
                .isKeyUp(true)
                .convertGender(null)
                .tuning(null)
                .capo(1)
                .build();

        FiltersOfDetailSong filters3 = FiltersOfDetailSong.builder()
                .key(null)
                .isKeyUp(null)
                .convertGender(true)
                .tuning(null)
                .capo(null)
                .build();

        FiltersOfDetailSong filters4 = FiltersOfDetailSong.builder()
                .key(null)
                .isKeyUp(null)
                .convertGender(null)
                .tuning(Tuning.HALF_STEP)
                .capo(null)
                .build();

        DetailSongDto detailSong = songService.getDetailSong(newSong, 0L, 10L, filters, "Db");
        DetailSongDto detailSong2 = songService.getDetailSong(newSong, 0L, 20L, filters, "Db");
        DetailSongDto detailSong3 = songService.getDetailSong(newSong, 0L, 20L, filters2, "Db");
        DetailSongDto detailSong5 = songService.getDetailSong(newSong, 0L, 10L, filters4, "Db");

        // then
        Assertions.assertEquals(10, detailSong.getContents().size());
        Assertions.assertEquals("C", detailSong.getCurrentKey());
        Assertions.assertEquals("D", detailSong2.getCurrentKey());
        Assertions.assertEquals("F", detailSong3.getCurrentKey());
        Assertions.assertEquals("C", detailSong5.getCurrentKey());

        Assertions.assertThrows(ImpossibleConvertGenderException.class, () -> songService.getDetailSong(newSong, 0L, 10L, filters3, "Db"));

        Assertions.assertEquals("C", detailSong.getContents().get(0).getChords().get(0));
        Assertions.assertEquals("G", detailSong.getContents().get(0).getChords().get(1));
        Assertions.assertEquals("Am7", detailSong.getContents().get(1).getChords().get(0));
    }

    @Test
    @DisplayName("기존 생성된 노래의 장르 또는 가사 또는 코드를 변경하면 반영된다.")
    void modifyExistSong() {

        // given
        Genre genre = Genre.builder().name("락").build();
        Genre genre2 = Genre.builder().name("발라드").build();
        Genre genre3 = Genre.builder().name("재즈").build();
        Genre genre4 = Genre.builder().name("팝").build();

        genreRepository.save(genre);
        genreRepository.save(genre2);
        genreRepository.save(genre3);
        genreRepository.save(genre4);

        Song song = new Song();
        song.changeRequestFields(
                "하늘을 달리다", "이적", "E", Gender.MALE, 116, null, null, null
        );

        List<String> genreList = new ArrayList<>();
        genreList.add("락");

        CreateSongDto songDto = CreateSongDto.builder()
                .title("하늘을 달리다")
                .artist("이적")
                .originalKey("E")
                .bpm(116)
                .gender(Gender.MALE)
                .modulation(null)
                .contents(null)
                .genres(genreList)
                .build();

        List<LyricsDto> lyricsDtoList = new ArrayList<>();
        lyricsDtoList.add(LyricsDto.builder()
                .tag("INTRO")
                .lyrics(null)
                .chords(LyricsDto.getAllChords("B", "A"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag("INTRO")
                .lyrics(null)
                .chords(LyricsDto.getAllChords("E", "A"))
                .build());

        lyricsDtoList.add(LyricsDto.builder()
                .tag("INTRO")
                .lyrics(null)
                .chords(LyricsDto.getAllChords("B", "A"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("두근거렸지 난 결국")
                .chords(LyricsDto.getAllChords("E", "Aadd2"))
                .build());
        songDto.setContents(lyricsDtoList);

        Long newSong = songService.createNewSong(songDto);

        CreateSongDto modifyDto = CreateSongDto.builder()
                .title("하늘을 달리다")
                .artist("이적")
                .originalKey("E")
                .bpm(116)
                .gender(Gender.FEMALE)
                .modulation(null)
                .contents(null)
                .build();

        List<LyricsDto> modifyLyrics = new ArrayList<>();
        modifyLyrics.add(LyricsDto.builder()
                .tag("INTRO")
                .lyrics(null)
                .chords(LyricsDto.getAllChords("B7", "A"))
                .build());
        modifyLyrics.add(LyricsDto.builder()
                .tag("INTRO")
                .lyrics(null)
                .chords(LyricsDto.getAllChords("E", "A/E"))
                .build());

        modifyLyrics.add(LyricsDto.builder()
                .tag("INTRO")
                .lyrics(null)
                .chords(LyricsDto.getAllChords("B", "A/E"))
                .build());
        modifyLyrics.add(LyricsDto.builder()
                .tag(null)
                .lyrics("국결 난 지렸거근두")
                .chords(LyricsDto.getAllChords("E", "Aadd2"))
                .build());
        modifyDto.setContents(modifyLyrics);

        genreList.clear();
        genreList.add("재즈");
        genreList.add("팝");

        modifyDto.setGenres(genreList);
        // when

        songService.modifySong(newSong, modifyDto);

        entityManager.flush();
        entityManager.clear();

        Song modifiedSong = songRepository.findById(newSong).get();

        // then

        Assertions.assertEquals(2, modifiedSong.getSongGenres().size());
        Assertions.assertEquals("재즈", modifiedSong.getSongGenres().get(0).getGenre().getName());
        Assertions.assertEquals("팝", modifiedSong.getSongGenres().get(1).getGenre().getName());
        Assertions.assertEquals(Gender.FEMALE, modifiedSong.getGender());
        Assertions.assertEquals("B7", modifiedSong.getLyricsList().get(0).getChords().get(0).getChord());
        Assertions.assertEquals("A/E", modifiedSong.getLyricsList().get(2).getChords().get(1).getChord());
        Assertions.assertEquals("국결 난 지렸거근두", modifiedSong.getLyricsList().get(3).getLyrics());
    }

    @Test
    @DisplayName("노래 데이터를 삭제하면 장르 중간 테이블과 가사, 코드 데이터는 모두 조회되지 않아야한다.")
    void deleteSong() {
        // given
        Genre genre = Genre.builder().name("락").build();
        genreRepository.save(genre);

        List<String> genreList = new ArrayList<>();
        genreList.add("락");

        CreateSongDto songDto = CreateSongDto.builder()
                .title("하늘을 달리다")
                .artist("이적")
                .originalKey("E")
                .bpm(116)
                .gender(Gender.MALE)
                .modulation(null)
                .contents(null)
                .genres(genreList)
                .build();

        List<LyricsDto> lyricsDtoList = new ArrayList<>();
        lyricsDtoList.add(LyricsDto.builder()
                .tag("INTRO")
                .lyrics(null)
                .chords(LyricsDto.getAllChords("B", "A"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag("INTRO")
                .lyrics(null)
                .chords(LyricsDto.getAllChords("E", "A"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag("INTRO")
                .lyrics(null)
                .chords(LyricsDto.getAllChords("B", "A"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("두근거렸지 난 결국")
                .chords(LyricsDto.getAllChords("E", "Aadd2"))
                .build());
        songDto.setContents(lyricsDtoList);

        Long newSong = songService.createNewSong(songDto);
        // when

        songService.removeSongData(newSong);

        entityManager.flush();
        entityManager.clear();

        // then
        Assertions.assertThrows(NoSuchDataException.class, () -> songService.getDetailSong(newSong, null, null, null, null));
        Assertions.assertEquals(0, lyricsRepository.findAll().size());
        Assertions.assertEquals(0, chordsRepository.findAll().size());
    }
}