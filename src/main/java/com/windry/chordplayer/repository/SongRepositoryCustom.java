package com.windry.chordplayer.repository;

import com.windry.chordplayer.domain.Song;
import com.windry.chordplayer.dto.FiltersOfSongList;
import com.windry.chordplayer.dto.SongListItemDto;

import java.util.List;

public interface SongRepositoryCustom {
    List<SongListItemDto> searchAllSong(FiltersOfSongList filtersOfSongList, Long page, Long size, Song cursorSong);
}
