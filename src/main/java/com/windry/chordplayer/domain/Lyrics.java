package com.windry.chordplayer.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Lyrics extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LYRICS_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SONG_ID")
    private Song song;

    @OneToMany(mappedBy = "lyrics", cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Chords> chords = new ArrayList<>();

    private String lyrics;

    @Column(nullable = false)
    private Integer line;

    @Enumerated(EnumType.STRING)
    private Tag tag;

    @Builder
    public Lyrics(String lyrics, Integer line, Tag tag) {
        this.lyrics = lyrics;
        this.line = line;
        this.tag = tag;
    }

    public void changeSong(Song song){
        this.song = song;
        song.getLyricsList().add(this);
    }

    public void addChords(Chords chord){
        chords.add(chord);
        chord.changeLyrics(this);
    }

}
