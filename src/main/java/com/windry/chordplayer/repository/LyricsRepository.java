package com.windry.chordplayer.repository;

import com.windry.chordplayer.domain.Lyrics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LyricsRepository extends JpaRepository<Lyrics, Long>, LyricsRepositoryCustom {
}
