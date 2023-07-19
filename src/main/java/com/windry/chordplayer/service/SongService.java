package com.windry.chordplayer.service;

import com.windry.chordplayer.domain.*;
import com.windry.chordplayer.dto.lyrics.DetailLyricsDto;
import com.windry.chordplayer.dto.lyrics.LyricsDto;
import com.windry.chordplayer.dto.song.*;
import com.windry.chordplayer.exception.*;
import com.windry.chordplayer.repository.GenreRepository;
import com.windry.chordplayer.repository.lyrics.LyricsRepository;
import com.windry.chordplayer.repository.song.SongRepository;
import com.windry.chordplayer.spec.Gender;
import com.windry.chordplayer.spec.Tag;
import com.windry.chordplayer.util.ChordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class SongService {

    private final SongRepository songRepository;
    private final LyricsRepository lyricsRepository;
    private final GenreRepository genreRepository;

    @Transactional
    public Long createNewSong(CreateSongDto createSongDto) {
        if (createSongDto == null)
            throw new InvalidInputException();

        // 제목 & 가수 중복 검증
        validateDupSongAndArtist(createSongDto.getTitle(), createSongDto.getArtist());

        // 유효한 키인지 검증
        if (isInvalidKey(createSongDto.getOriginalKey()))
            throw new InvalidKeyException();

        Song song = Song.builder()
                .title(createSongDto.getTitle())
                .artist(createSongDto.getArtist())
                .gender(createSongDto.getGender())
                .originalKey(createSongDto.getOriginalKey())
                .modulation(createSongDto.getModulation())
                .note(createSongDto.getNote())
                .bpm(createSongDto.getBpm())
                .build();

        // 장르 데이터 검색해서 저장
        createSongDto.getGenres().forEach(genre -> {
            Optional<Genre> genreOptional = genreRepository.findGenreByName(genre);

            if (genreOptional.isPresent()) {
                song.addGenre(SongGenre.builder()
                        .genre(genreOptional.get())
                        .song(song).build());
            } else
                throw new NoSuchDataException();
        });

        // 가사 및 코드 엔티티 생성
        List<LyricsDto> contents = createSongDto.getContents();
        IntStream.range(0, contents.size())
                .forEach(index -> {
                    // 가사 엔티티 객체 생성
                    Lyrics lyrics = Lyrics.builder()
                            .tag(Tag.findTagByString(contents.get(index).getTag()))
                            .line(index + 1)
                            .lyrics(contents.get(index).getLyrics())
                            .build();
                    // 코드 객체 생성 후, 가사 엔티티에 추가
                    contents.get(index).getChords().forEach(chord ->
                            lyrics.addChords(Chords.builder()
                                    .chord(chord)
                                    .build())
                    );
                    // 노래 엔티티에 최종 가사 엔티티 저장
                    song.addLyrics(lyrics);
                });

        return songRepository.save(song).getId();
    }

    public List<SongListItemDto> getAllSongs(FiltersOfSongList filtersOfSongList, Long page, Long size) {
        if (size == null || size < 1)
            throw new InvalidInputException();

        Song currentSong = null;
        if (page != null && page >= 0 && page != 0) {
            currentSong = songRepository.findById(page).orElseThrow(NoSuchDataException::new);
        }
        return songRepository.searchAllSong(filtersOfSongList, page, size, currentSong);
    }

    @Transactional
    public DetailSongDto getDetailSong(Long songId, Long offset, Long size, FiltersOfDetailSong filtersOfDetailSong, String curKey) {

        Optional<Song> optional = songRepository.findById(songId);
        if (optional.isEmpty())
            throw new NoSuchDataException();

        if (size == null || size < 1)
            throw new InvalidInputException();

        if (curKey == null)
            throw new InvalidInputException();

        // 현재 키가 올바르지 않은지 검증
        if (isInvalidKey(curKey))
            throw new InvalidKeyException();

        Song song = optional.get();

        int cumulateKey = 0; // 최종 키 변경 적용에 대한 변경할 누적 키 값
        String currentKey; // 키 변경 적용에 대한 최종 키
        Gender currentGender = song.getGender();

        List<DetailLyricsDto> lyrics = lyricsRepository.getPagingLyricsBySong(offset, size, songId);

        // 카포 적용 -> 키 낮춤
        if (filtersOfDetailSong.getCapo() != null) {
            cumulateKey += filtersOfDetailSong.getCapo() * -1;
        }

        // 직접 키 변경
        if (filtersOfDetailSong.getIsKeyUp() != null) {
            if (filtersOfDetailSong.getKey() == null) {
                throw new NoSuchDataException();
            }
            int amount = filtersOfDetailSong.getIsKeyUp() ? filtersOfDetailSong.getKey() : filtersOfDetailSong.getKey() * -1;
            cumulateKey += amount;
        }

        // 남/여 키 변경 -> ±5
        if (filtersOfDetailSong.getConvertGender() != null && filtersOfDetailSong.getConvertGender()) {
            if (!optional.get().getGender().equals(Gender.MIXED)) {
                int amount = 0;
                if (optional.get().getGender().equals(Gender.MALE)) {
                    amount = 5;
                    currentGender = Gender.FEMALE;
                } else {
                    amount = -5;
                    currentGender = Gender.MALE;
                }
                cumulateKey += amount;
            } else {
                throw new ImpossibleConvertGenderException();
            }
        }

        // 튜닝 변경 (하프 or 전체) -> 키 낮춤
        if (filtersOfDetailSong.getTuning() != null) {
            int amount = 0;
            switch (filtersOfDetailSong.getTuning()) {
                case HALF_STEP -> amount = -1;
                case WHOLE_STEP -> amount = -2;
            }
            cumulateKey += amount;
        }

        if (cumulateKey != 0) {
            applyKeyChange(lyrics, cumulateKey); // 모든 코드 키 변경 적용
        }
        /*
            전조가 3번 이상인 경우
            ex) Db-Eb-F-G
            현재 key가 Eb이라면 현재 가사 구간에서 MODULATION 발생하면 F 키로 변경
         */
        currentKey = findNextModulationKey(curKey, song, lyrics);

        currentKey = ChordUtil.changeKey(currentKey, cumulateKey);
        song.updateViewCount(); // 조회수 증가

        return DetailSongDto.builder()
                .artist(song.getArtist())
                .title(song.getTitle())
                .gender(currentGender)
                .currentKey(currentKey)
                .contents(lyrics)
                .build();
    }

    @Transactional
    public void modifySong(Long songId, CreateSongDto createSongDto) {
        Optional<Song> optional = songRepository.findById(songId);
        if (optional.isEmpty())
            throw new NoSuchDataException();

        if (createSongDto == null)
            throw new InvalidInputException();

        Song song = optional.get();

        // 제목 & 가수 중복 검증
        if (!(song.getTitle().equals(createSongDto.getTitle())
                && song.getArtist().equals(createSongDto.getArtist())))
            validateDupSongAndArtist(createSongDto.getTitle(), createSongDto.getArtist());

        // 유효한 키인지 검증
        if (isInvalidKey(createSongDto.getOriginalKey()))
            throw new InvalidKeyException();


        List<SongGenre> songGenres = new ArrayList<>();

        // songGenre 엔티티 추가
        createSongDto.getGenres().forEach(g -> {
            Optional<Genre> genre = genreRepository.findGenreByName(g);

            if (genre.isPresent()) {
                songGenres.add(SongGenre.builder()
                        .genre(genre.get())
                        .song(song)
                        .build());
            } else
                throw new NoSuchDataException();
        });

        List<Lyrics> lyricsList = new ArrayList<>();

        // 새로 가사 및 코드 데이터 가져오고 가사 엔티티 생성
        List<LyricsDto> contents = createSongDto.getContents();
        IntStream.range(0, contents.size())
                .forEach(index -> {
                    Lyrics lyrics = Lyrics.builder()
                            .line(index + 1)
                            .lyrics(contents.get(index).getLyrics())
                            .tag(Tag.findTagByString(contents.get(index).getTag()))
                            .build();
                    lyrics.changeAllChords(contents.get(index).getChords());
                    lyricsList.add(lyrics);
                });

        // 필드 업데이트
        song.changeRequestFields(
                createSongDto.getTitle(),
                createSongDto.getArtist(),
                createSongDto.getOriginalKey(),
                createSongDto.getGender(),
                createSongDto.getBpm(),
                createSongDto.getModulation(),
                createSongDto.getNote()
        );
        song.updateLyrics(lyricsList);
        song.updateGenres(songGenres);
    }

    @Transactional
    public void removeSongData(Long songId) {
        Optional<Song> optional = songRepository.findById(songId);
        if (optional.isEmpty())
            throw new NoSuchDataException();

        Song song = optional.get();
        songRepository.delete(song);
    }

    private String findNextModulationKey(String curKey, Song song, List<DetailLyricsDto> lyrics) {
        Optional<String> result = lyrics.stream() // 주어진 리스트에 대해
                .filter(lyric -> lyric.getTag() != null && lyric.getTag().equals(Tag.MODULATION)) // 아이템 중 필터링
                .findFirst() // 그 중 처음에 찾은 것
                .map(lyric -> { // 찾은 것을 새롭게 매핑
                    String[] split = song.getModulation().split("-");
                    return IntStream.range(0, split.length)
                            .filter(index -> curKey.equals(split[index]))
                            .mapToObj(index -> split[index + 1])
                            .findFirst()
                            .orElse(null);
                });

        return result.orElse(song.getOriginalKey());
    }

    public void validateDupSongAndArtist(String title, String artist) {
        if (title == null || artist == null)
            throw new InvalidInputException();

        // 공백 무시하고 검색 가능
        Optional<Song> song = songRepository.findSongByTitleAndArtist(title.replace(" ", ""), artist.replace(" ", ""));
        if (song.isPresent())
            throw new DuplicateTitleAndArtistException();
    }

    private void applyKeyChange(List<DetailLyricsDto> lyrics, int amount) {
        lyrics.forEach(detail -> {
            List<String> chords = new ArrayList<>(detail.getChords());
            chords.replaceAll(originChord -> ChordUtil.changeKey(originChord, amount));
            detail.setChords(chords);
        });
    }

    private boolean isInvalidKey(String currentKey) {
        Pattern pattern = Pattern.compile("^[A-G]([#b]?)(m)?$");
        Matcher matcher = pattern.matcher(currentKey);
        return !matcher.matches();
    }
}
