package com.windry.chordplayer.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Song extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SONG_ID")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String artist;

    @Column(nullable = false, length = 3)
    private String originalKey;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Integer bpm;

    private String modulation;

    @OneToMany(mappedBy = "song", cascade = CascadeType.ALL)
    private List<Lyrics> lyricsList;

    @Builder
    public Song(String title, String artist, String originalKey, Gender gender, Integer bpm, String modulation) {
        this.title = title;
        this.artist = artist;
        this.originalKey = originalKey;
        this.gender = gender;
        this.bpm = bpm;
        this.modulation = modulation;
    }
}
