package com.windry.chordplayer.repository;

import com.windry.chordplayer.domain.Chords;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChordsRepository extends JpaRepository<Chords, Long> {
}
