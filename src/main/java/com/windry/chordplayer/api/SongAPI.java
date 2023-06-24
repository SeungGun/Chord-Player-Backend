package com.windry.chordplayer.api;

import com.windry.chordplayer.domain.Gender;
import com.windry.chordplayer.domain.SearchCriteria;
import com.windry.chordplayer.domain.SortStrategy;
import com.windry.chordplayer.domain.Tuning;
import com.windry.chordplayer.dto.CreateSongDto;
import com.windry.chordplayer.dto.FiltersOfSongList;
import com.windry.chordplayer.dto.SongListItemDto;
import com.windry.chordplayer.exception.InvalidInputException;
import com.windry.chordplayer.service.SongService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        return ResponseEntity.ok().body(song);
    }

    @GetMapping("")
    public ResponseEntity<List<SongListItemDto>> getSongList(
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size,
            @RequestParam(value = "searchCriteria", required = false) SearchCriteria searchCriteria,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "gender", required = false) Gender gender,
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "genre", required = false) String genre,
            @RequestParam(value = "sort", required = false) SortStrategy sortStrategy
    ) {

        if(page == null || size == null)
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
//
//    @GetMapping("/{songId}")
//    public ResponseEntity<Void> getFilteredSong(
//            @PathVariable("songId") Long songId,
//            @RequestParam(value = "capo", required = false) int capo,
//            @RequestParam(value = "tuning", required = false)Tuning tuning,
//            @RequestParam(value = "gender", required = false) Boolean convertGender,
//            @RequestParam(value = "key-up", required = false) Boolean isKeyUp,
//            @RequestParam(value = "key",required = false) int key,
//            @RequestParam("offset") int offset,
//            @RequestParam("size") int size
//            ){
//
//    }
//
//    @PutMapping("/{songId}")
//    public ResponseEntity<Void> modifySong(){
//
//    }
//
//    @DeleteMapping("/{songId}")
//    public ResponseEntity<Void> deleteSong(){
//
//    }

}
