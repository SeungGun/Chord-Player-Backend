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

    public void changeKey(int amount){
        this.chord = this.chord.replace(this.chord, ChordUtil.notes.get(ChordUtil.notes.indexOf(this.chord) + amount));

    }
}
