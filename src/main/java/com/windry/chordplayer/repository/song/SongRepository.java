package com.windry.chordplayer.repository.song;

import com.windry.chordplayer.domain.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SongRepository extends JpaRepository<Song, Long>, SongRepositoryCustom {

    @Query("select s from Song s where replace(s.title, ' ', '') = :title and replace(s.artist, ' ', '') = :artist ")
    Optional<Song> findSongByTitleAndArtist(@Param("title") String title, @Param("artist") String artist);
}
