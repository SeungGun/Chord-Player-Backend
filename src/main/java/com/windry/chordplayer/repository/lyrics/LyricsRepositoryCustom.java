package com.windry.chordplayer.repository.lyrics;

import com.windry.chordplayer.dto.lyrics.DetailLyricsDto;

import java.util.List;

public interface LyricsRepositoryCustom {

    List<DetailLyricsDto> getPagingLyricsBySong(Long offset, Long size, Long songId);
}
