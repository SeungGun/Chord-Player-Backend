package com.windry.chordplayer.repository.song;

import com.windry.chordplayer.domain.Song;
import com.windry.chordplayer.dto.song.FiltersOfSongList;
import com.windry.chordplayer.dto.song.SongListItemDto;

import java.util.List;

public interface SongRepositoryCustom {
    List<SongListItemDto> searchAllSong(FiltersOfSongList filtersOfSongList, Long page, Long size, Song cursorSong);
}
