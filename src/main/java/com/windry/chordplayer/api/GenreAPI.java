package com.windry.chordplayer.api;

import com.windry.chordplayer.dto.genre.CreateGenreDto;
import com.windry.chordplayer.dto.genre.GenreListDto;
import com.windry.chordplayer.service.GenreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
@Slf4j
public class GenreAPI {

    private final GenreService genreService;

    @PostMapping("")
    public ResponseEntity<Map<String, Long>> registerNewGenre(
            @RequestBody CreateGenreDto createGenreDto
    ) {
        Long genreId = genreService.createGenre(createGenreDto);
        Map<String, Long> result = new HashMap<>();
        result.put("genreId", genreId);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("")
    public ResponseEntity<List<GenreListDto>> getGenreList() {
        List<GenreListDto> genreList = genreService.getGenreList();
        return ResponseEntity.ok(genreList);
    }

    @DeleteMapping("/{genreId}")
    public ResponseEntity<Void> removeGenre(
            @PathVariable("genreId") Long genreId
    ) {
        genreService.removeGenre(genreId);
        return ResponseEntity.ok().build();
    }
}
