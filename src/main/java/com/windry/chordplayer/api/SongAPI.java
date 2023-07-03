package com.windry.chordplayer.api;

import com.windry.chordplayer.dto.*;
import com.windry.chordplayer.spec.Gender;
import com.windry.chordplayer.spec.SearchCriteria;
import com.windry.chordplayer.spec.SortStrategy;
import com.windry.chordplayer.exception.InvalidInputException;
import com.windry.chordplayer.service.SongService;
import com.windry.chordplayer.spec.Tuning;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/songs")
@Slf4j
public class SongAPI {

    private final SongService songService;

    @PostMapping("")
    public ResponseEntity<Long> createSongWithChords(
            @RequestBody CreateSongDto createSongDto
    ) {
        Long song = songService.createNewSong(createSongDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(song);
    }

    @GetMapping("")
    public ResponseEntity<List<SongListItemDto>> getSongList(
            @RequestParam(value = "page", defaultValue = "1") Long page,
            @RequestParam(value = "size", defaultValue = "10") Long size,
            @RequestParam(value = "searchCriteria", required = false) SearchCriteria searchCriteria,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "gender", required = false) Gender gender,
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "genre", required = false) String genre,
            @RequestParam(value = "sort", required = false) SortStrategy sortStrategy
    ) {

        if (page == null || size == null)
            throw new InvalidInputException();

        FiltersOfSongList filters = FiltersOfSongList.builder()
                .searchCriteria(searchCriteria)
                .searchKeyword(keyword)
                .gender(gender)
                .sortStrategy(sortStrategy)
                .searchGenre(genre)
                .searchKey(key)
                .build();

        List<SongListItemDto> allSongs = songService.getAllSongs(filters, page, size);

        return ResponseEntity.ok().body(allSongs);
    }

    @GetMapping("/{songId}")
    public ResponseEntity<DetailSongDto> getFilteredSong(
            @PathVariable("songId") Long songId,
            @RequestParam(value = "capo", required = false) Integer capo,
            @RequestParam(value = "tuning", required = false) Tuning tuning,
            @RequestParam(value = "gender", required = false) Boolean convertGender,
            @RequestParam(value = "key-up", required = false) Boolean isKeyUp,
            @RequestParam(value = "key", required = false) Integer key,
            @RequestParam(value = "currentKey") String currentKey,
            @RequestParam(value = "offset", defaultValue = "0") Long offset, // line 에 대한 offset
            @RequestParam(value = "size", defaultValue = "20") Long size
    ) {

        if (songId == null)
            throw new InvalidInputException();

        if (offset == null || size == null)
            throw new InvalidInputException();

        if (currentKey == null)
            throw new InvalidInputException();

        FiltersOfDetailSong filters = FiltersOfDetailSong.builder()
                .capo(capo)
                .tuning(tuning)
                .convertGender(convertGender)
                .isKeyUp(isKeyUp)
                .key(key)
                .build();

        DetailSongDto detailSong = songService.getDetailSong(songId, offset, size, filters, currentKey);
        return ResponseEntity.ok().body(detailSong);
    }

    @PutMapping("/{songId}")
    public ResponseEntity<Void> modifySong(
            @PathVariable("songId") Long songId,
            @RequestBody CreateSongDto createSongDto
    ) {
        songService.modifySong(songId, createSongDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{songId}")
    public ResponseEntity<Void> deleteSong(
            @PathVariable("songId") Long songId
    ) {
        songService.removeSongData(songId);
        return ResponseEntity.ok().build();
    }

}
