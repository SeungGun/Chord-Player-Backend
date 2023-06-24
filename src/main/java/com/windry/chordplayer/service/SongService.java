package com.windry.chordplayer.service;

import com.windry.chordplayer.domain.*;
import com.windry.chordplayer.dto.CreateSongDto;
import com.windry.chordplayer.dto.FiltersOfSongList;
import com.windry.chordplayer.dto.LyricsDto;
import com.windry.chordplayer.dto.SongListItemDto;
import com.windry.chordplayer.exception.DuplicateTitleAndArtistException;
import com.windry.chordplayer.exception.InvalidInputException;
import com.windry.chordplayer.repository.ChordsRepository;
import com.windry.chordplayer.repository.GenreRepository;
import com.windry.chordplayer.repository.LyricsRepository;
import com.windry.chordplayer.repository.SongRepository;
import com.windry.chordplayer.spec.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<SongListItemDto> getAllSongs(FiltersOfSongList filtersOfSongList, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return songRepository.searchAllSong(filtersOfSongList, pageRequest);
    }

    public void validateDupSongAndArtist(String title, String artist) {
        if (title == null || artist == null)
            throw new InvalidInputException();

        // 공백 무시하고 검색 가능
        Optional<Song> song = songRepository.findSongByTitleAndArtist(title.replace(" ", ""), artist.replace(" ", ""));
        if (song.isPresent())
            throw new DuplicateTitleAndArtistException();
    }
}
