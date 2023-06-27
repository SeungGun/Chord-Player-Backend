package com.windry.chordplayer.service;

import com.windry.chordplayer.domain.*;
import com.windry.chordplayer.dto.*;
import com.windry.chordplayer.exception.DuplicateTitleAndArtistException;
import com.windry.chordplayer.exception.ImpossibleConvertGenderException;
import com.windry.chordplayer.exception.InvalidInputException;
import com.windry.chordplayer.exception.NoSuchDataException;
import com.windry.chordplayer.repository.ChordsRepository;
import com.windry.chordplayer.repository.GenreRepository;
import com.windry.chordplayer.repository.LyricsRepository;
import com.windry.chordplayer.repository.SongRepository;
import com.windry.chordplayer.spec.Gender;
import com.windry.chordplayer.spec.Tag;
import com.windry.chordplayer.util.ChordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SongService {

    private final SongRepository songRepository;
    private final LyricsRepository lyricsRepository;
    private final ChordsRepository chordsRepository;
    private final GenreRepository genreRepository;

    @Transactional
    public Long createNewSong(CreateSongDto createSongDto) {

        if (createSongDto == null)
            throw new InvalidInputException();

        validateDupSongAndArtist(createSongDto.getTitle(), createSongDto.getArtist());

        Song song = Song.builder()
                .title(createSongDto.getTitle())
                .artist(createSongDto.getArtist())
                .gender(createSongDto.getGender())
                .originalKey(createSongDto.getOriginalKey())
                .modulation(createSongDto.getModulation())
                .note(createSongDto.getNote())
                .bpm(createSongDto.getBpm())
                .build();

        for (int i = 0; i < createSongDto.getGenres().size(); ++i) {
            Optional<Genre> genreOptional = genreRepository.findGenreByName(createSongDto.getGenres().get(i));
            if (genreOptional.isPresent()) {
                SongGenre songGenre = SongGenre.builder()
                        .genre(genreOptional.get())
                        .song(song).build();
                song.addGenre(songGenre);
            }
        }
        for (int i = 0; i < createSongDto.getContents().size(); i++) {
            LyricsDto lyricsDto = createSongDto.getContents().get(i);
            Lyrics lyrics = Lyrics.builder()
                    .tag(Tag.findTagByString(lyricsDto.getTag()))
                    .line(i + 1)
                    .lyrics(lyricsDto.getLyrics())
                    .build();

            for (String chord : lyricsDto.getChords()) {
                Chords chords = Chords
                        .builder()
                        .chord(chord)
                        .build();
                lyrics.addChords(chords);
            }
            song.addLyrics(lyrics);
        }
        return songRepository.save(song).getId();
    }

    public List<SongListItemDto> getAllSongs(FiltersOfSongList filtersOfSongList, Long page, Long size) {
        Optional<Song> optionalSong = songRepository.findById(page);
        if (optionalSong.isEmpty())
            throw new NoSuchDataException();
        return songRepository.searchAllSong(filtersOfSongList, page, size, optionalSong.get());
    }

    @Transactional
    public DetailSongDto getDetailSong(Long songId, Long offset, Long size, FiltersOfDetailSong filtersOfDetailSong, String curKey) {

        Optional<Song> optional = songRepository.findById(songId);
        if (optional.isEmpty())
            throw new NoSuchDataException();

        Song song = optional.get();

        int cumulateKey = 0;
        String currentKey;
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
        if (filtersOfDetailSong.getConvertGender() != null) {
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

        List<SongGenre> songGenres = new ArrayList<>();
        for (int i = 0; i < createSongDto.getGenres().size(); ++i) {
            Optional<Genre> genre = genreRepository.findGenreByName(createSongDto.getGenres().get(i));
            if (genre.isPresent()) {
                SongGenre songGenre = SongGenre.builder()
                        .genre(genre.get())
                        .song(song)
                        .build();
                songGenres.add(songGenre);
            }
        }

        List<Lyrics> lyricsList = new ArrayList<>();
        for (int i = 0; i < createSongDto.getContents().size(); ++i) {
            Lyrics lyrics = Lyrics.builder()
                    .line(i + 1)
                    .lyrics(createSongDto.getContents().get(i).getLyrics())
                    .tag(Tag.findTagByString(createSongDto.getContents().get(i).getTag()))
                    .build();
            lyrics.changeAllChords(createSongDto.getContents().get(i).getChords());
            lyricsList.add(lyrics);
        }

        song.changeRequestFields(
                createSongDto.getTitle(),
                createSongDto.getArtist(),
                createSongDto.getOriginalKey(),
                createSongDto.getGender(),
                createSongDto.getBpm(),
                createSongDto.getModulation(),
                lyricsList,
                songGenres
        );
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
        for (DetailLyricsDto lyric : lyrics) {
            if (lyric.getTag() != null && lyric.getTag().equals(Tag.MODULATION)) {
                String[] split = song.getModulation().split("-");
                for (int j = 0; j < split.length; ++j) {
                    if (curKey.equals(split[j])) {
                        return split[j + 1];
                    }
                }
            }
        }
        return song.getOriginalKey();
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
        for (DetailLyricsDto detailLyricsDto : lyrics) {
            List<String> chords = new ArrayList<>(detailLyricsDto.getChords());
            chords.replaceAll(originChord -> ChordUtil.changeKey(originChord, amount));
            detailLyricsDto.setChords(chords);
        }
    }
}
