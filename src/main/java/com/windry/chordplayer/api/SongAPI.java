package com.windry.chordplayer.api;

import com.windry.chordplayer.dto.song.*;
import com.windry.chordplayer.spec.Gender;
import com.windry.chordplayer.spec.SearchCriteria;
import com.windry.chordplayer.spec.SortStrategy;
import com.windry.chordplayer.service.SongService;
import com.windry.chordplayer.spec.Tuning;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/songs")
@Slf4j
public class SongAPI {

    private final SongService songService;

    @PostMapping("")
    public ResponseEntity<Map<String, Long>> createSongWithChords(
            @RequestBody CreateSongDto createSongDto
    ) {
        Long songId = songService.createNewSong(createSongDto);
        Map<String, Long> result = new HashMap<>();
        result.put("songId", songId);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("")
    public ResponseEntity<List<SongListItemDto>> getSongList(
            @RequestParam(value = "page", defaultValue = "0") Long page,
            @RequestParam(value = "size", defaultValue = "10") Long size,
            @RequestParam(value = "searchCriteria", required = false) String searchCriteria, // 검색 기준
            @RequestParam(value = "keyword", required = false) String keyword, // 검색어
            @RequestParam(value = "gender", required = false) String gender, // 필터링할 성별
            @RequestParam(value = "key", required = false) String key, // 필터링할 키
            @RequestParam(value = "genre", required = false) String genre, // 필터링할 장르
            @RequestParam(value = "sort", required = false) String sortStrategy // 정렬 기준
    ) {
        FiltersOfSongList filters = FiltersOfSongList.builder()
                .searchCriteria(SearchCriteria.findMatchedEnumFromString(searchCriteria))
                .searchKeyword(keyword)
                .gender(Gender.findMatchedEnumFromString(gender))
                .sortStrategy(SortStrategy.findMatchedEnumFromString(sortStrategy))
                .searchGenre(genre)
                .searchKey(key)
                .build();
        return ResponseEntity.ok().body(songService.getAllSongs(filters, page, size));
    }

    @GetMapping("/{songId}")
    public ResponseEntity<DetailSongDto> getFilteredSong(
            @PathVariable("songId") Long songId,
            @RequestParam(value = "offset", defaultValue = "0") Long offset, // 마디에 대한 offset
            @RequestParam(value = "size", defaultValue = "20") Long size, // 가져올 마디의 개수
            @RequestParam(value = "currentKey") String currentKey, // 현재 마디의 키 값
            @RequestParam(value = "capo", required = false) Integer capo, // 기타 카포 적용할 프렛
            @RequestParam(value = "tuning", required = false) String tuning, // 기타 튜닝 방식
            @RequestParam(value = "gender", required = false) Boolean convertGender, // 성별 변경할지 여부
            @RequestParam(value = "key-up", required = false) Boolean isKeyUp, // 키를 높일지 여부
            @RequestParam(value = "key", required = false) Integer key // 변경할 키 값
    ) {
        FiltersOfDetailSong filters = FiltersOfDetailSong.builder()
                .capo(capo)
                .tuning(Tuning.findMatchedEnumFromString(tuning))
                .convertGender(convertGender)
                .isKeyUp(isKeyUp)
                .key(key)
                .build();
        return ResponseEntity.ok().body(songService.getDetailSong(songId, offset, size, filters, currentKey));
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
