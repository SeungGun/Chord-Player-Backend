package com.windry.chordplayer.repository;

import com.windry.chordplayer.dto.FiltersOfSongList;
import com.windry.chordplayer.dto.SongListItemDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SongRepositoryCustom {
    List<SongListItemDto> searchAllSong(FiltersOfSongList filtersOfSongList, Pageable pageable);
}
