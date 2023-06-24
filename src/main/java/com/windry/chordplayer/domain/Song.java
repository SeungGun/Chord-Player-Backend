package com.windry.chordplayer.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(
        uniqueConstraints = @UniqueConstraint(
                name = "no_dup_title_with_artist",
                columnNames = {"title", "artist"}
        )
)
public class Song extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SONG_ID")
    private Long id;

    @Column(nullable = false, name = "title")
    private String title;

    @Column(nullable = false, name = "artist")
    private String artist;

    @Column(nullable = false, length = 3)
    private String originalKey;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Integer bpm;

    private String modulation;

    @ColumnDefault("0")
    private int viewCount;

    private String note;

    @OneToMany(mappedBy = "song", cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<SongGenre> songGenres = new ArrayList<>();

    @OneToMany(mappedBy = "song", cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Lyrics> lyricsList = new ArrayList<>();

    @Builder
    public Song(String title, String artist, String originalKey, Gender gender, Integer bpm, String modulation, String note) {
        this.title = title;
        this.artist = artist;
        this.originalKey = originalKey;
        this.gender = gender;
        this.bpm = bpm;
        this.modulation = modulation;
        this.note = note;
    }

    public void changeRequestFields(String title, String artist, String originalKey, Gender gender, Integer bpm, String modulation, List<Lyrics> lyrics) {
        this.title = title;
        this.artist = artist;
        this.originalKey = originalKey;
        this.gender = gender;
        this.bpm = bpm;
        this.modulation = modulation;
        this.lyricsList = lyrics;
    }

    public void addLyrics(Lyrics lyrics) {
        lyricsList.add(lyrics);
        lyrics.changeSong(this);
    }

    public void addGenre(SongGenre songGenre) {
        songGenres.add(songGenre);
    }
}
