package com.windry.chordplayer.repository;

import com.windry.chordplayer.dto.DetailLyricsDto;

import java.util.List;

public interface LyricsRepositoryCustom {

    List<DetailLyricsDto> getPagingLyricsBySong(Long offset, Long size, Long songId);
}
