package com.windry.chordplayer.domain;

import com.windry.chordplayer.dto.ChordUtil;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Chords extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CHORDS_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LYRICS_ID")
    private Lyrics lyrics;

    @Column(nullable = false)
    private String chord;

    @Builder
    public Chords(String chord) {
        this.chord = chord;
    }

    public void changeKey(int amount) {
        this.chord = this.chord.replace(this.chord, ChordUtil.sharpNotes.get(ChordUtil.sharpNotes.indexOf(this.chord) + amount));
    }

    public void changeLyrics(Lyrics lyrics) {
        this.lyrics = lyrics;
        lyrics.getChords().add(this);
    }
}
