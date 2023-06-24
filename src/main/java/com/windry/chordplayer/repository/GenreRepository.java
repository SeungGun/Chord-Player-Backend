package com.windry.chordplayer.repository;

import com.windry.chordplayer.domain.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    Optional<Genre> findGenreByName(String name);
}
