package com.windry.chordplayer.service;

import com.windry.chordplayer.domain.Chords;
import com.windry.chordplayer.domain.Lyrics;
import com.windry.chordplayer.domain.Song;
import com.windry.chordplayer.domain.Tag;
import com.windry.chordplayer.dto.CreateSongDto;
import com.windry.chordplayer.dto.LyricsDto;
import com.windry.chordplayer.exception.DuplicateTitleAndArtistException;
import com.windry.chordplayer.exception.InvalidInputException;
import com.windry.chordplayer.repository.ChordsRepository;
import com.windry.chordplayer.repository.LyricsRepository;
import com.windry.chordplayer.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class SongService {

    private final SongRepository songRepository;
    private final LyricsRepository lyricsRepository;
    private final ChordsRepository chordsRepository;

    @Transactional
    public Long createNewSong(CreateSongDto createSongDto) {

        validateDupSongAndArtist(createSongDto.getTitle(), createSongDto.getArtist());

        Song song = Song.builder()
                .title(createSongDto.getTitle())
                .artist(createSongDto.getArtist())
                .gender(createSongDto.getGender())
                .originalKey(createSongDto.getOriginalKey())
                .modulation(createSongDto.getModulation())
                .bpm(createSongDto.getBpm())
                .build();

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
                chords.changeLyrics(lyrics);
                chordsRepository.save(chords);
            }
            lyrics.changeSong(song);
            lyricsRepository.save(lyrics);
        }
        return songRepository.save(song).getId();
    }

    public void validateDupSongAndArtist(String title, String artist) {
        if (title == null || artist == null)
            throw new InvalidInputException();

        // 공백 무시하고 검색 가능
        Optional<Song> song = songRepository.findSongByTitleAndArtist(title.trim(), artist.trim());
        if (song.isPresent())
            throw new DuplicateTitleAndArtistException();
    }
}
