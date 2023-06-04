package com.windry.chordplayer.service;

import com.windry.chordplayer.domain.Gender;
import com.windry.chordplayer.domain.Song;
import com.windry.chordplayer.dto.CreateSongDto;
import com.windry.chordplayer.dto.LyricsDto;
import com.windry.chordplayer.exception.DuplicateTitleAndArtistException;
import com.windry.chordplayer.repository.ChordsRepository;
import com.windry.chordplayer.repository.LyricsRepository;
import com.windry.chordplayer.repository.SongRepository;
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
    private SongService songService;


    @DisplayName("노래 데이터를 생성할 때, 중복되는 제목과 가수가 있을 경우 예외를 발생한다.")
    @Test
    void duplicateSongTitleAndArtistException() {
        // given
        Song song = new Song();
        CreateSongDto songDto = CreateSongDto.builder()
                .title("하늘을 달리다")
                .artist("이적")
                .originalKey("E")
                .bpm(116)
                .gender(Gender.MALE)
                .modulation(null)
                .contents(null)
                .build();

        song.changeRequestFields(
                songDto.getTitle(),
                songDto.getArtist(),
                songDto.getOriginalKey(),
                songDto.getGender(),
                songDto.getBpm(),
                songDto.getModulation(),
                null);

        Song differentArtistSong = new Song();
        differentArtistSong.changeRequestFields(
                songDto.getTitle(),
                "허각",
                songDto.getOriginalKey(),
                songDto.getGender(),
                songDto.getBpm(),
                songDto.getModulation(),
                null
        );
        // when
        songRepository.save(song);
        songRepository.save(differentArtistSong);

        // then
        Assertions.assertEquals("허각", songRepository.findById(2L).get().getArtist());
        Assertions.assertThrows(DuplicateTitleAndArtistException.class, () -> songService.validateDupSongAndArtist("하늘을달리다", "이적"));
    }

    @DisplayName("4박자 마디의 가사와 코드가 포함된 새로운 노래 데이터를 생성한다.")
    @Test
    void createNewSong() {
        // given
        Song song = new Song();
        song.changeRequestFields(
                "하늘을 달리다", "이적", "E", Gender.MALE, 116, null, null
        );

        CreateSongDto songDto = CreateSongDto.builder()
                .title("하늘을 달리다")
                .artist("이적")
                .originalKey("E")
                .bpm(116)
                .gender(Gender.MALE)
                .modulation(null)
                .contents(null)
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

        // then
        org.assertj.core.api.Assertions.assertThat(song)
                .usingRecursiveComparison()
                .ignoringFields("createdDate", "modifiedDate", "id", "lyricsList")
                .isEqualTo(songRepository.findById(newSong).get());
    }
}