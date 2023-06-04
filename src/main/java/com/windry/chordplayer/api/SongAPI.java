package com.windry.chordplayer.api;

import com.windry.chordplayer.domain.Tuning;
import com.windry.chordplayer.dto.CreateSongDto;
import com.windry.chordplayer.service.SongService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
//
//    @GetMapping("")
//    public ResponseEntity<Void> getSongList(){
//
//    }
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
