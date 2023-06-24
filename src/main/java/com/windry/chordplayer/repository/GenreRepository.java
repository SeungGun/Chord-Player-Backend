package com.windry.chordplayer.repository;

import com.windry.chordplayer.domain.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    Genre findGenreByName(String name);
}
