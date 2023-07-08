package com.windry.chordplayer.domain;

import com.windry.chordplayer.spec.Tag;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @OneToMany(mappedBy = "lyrics", cascade = CascadeType.ALL, orphanRemoval = true)
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

    public void changeSong(Song song) {
        this.song = song;
    }

    public void addChords(Chords chord) {
        chords.add(chord);
        chord.changeLyrics(this);
    }

    public void changeAllChords(List<String> chords) {
        this.chords = chords.stream().map(c -> Chords.builder().chord(c).build()).toList();
    }

    public List<String> convertStringChords(List<Chords> chords) {
        return chords.stream().map(Chords::getChord).toList();
    }

    public void updateTag(Tag tag) {
        this.tag = tag;
    }

    public void updateLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public void updateChords(List<Chords> newChords) {
        // 코드 업데이트
        for (int i = 0; i < this.chords.size(); ++i) {
            if (i < newChords.size()) {
                Chords existChord = this.chords.get(i);
                Chords updatedChord = newChords.get(i);
                existChord.updateChord(updatedChord.getChord());
            } else {
                this.chords.remove(i);
                i--;
            }
        }

        for (int i = this.chords.size(); i < newChords.size(); ++i) {
            Chords newChord = newChords.get(i);
            this.chords.add(newChord);
        }
    }
}
