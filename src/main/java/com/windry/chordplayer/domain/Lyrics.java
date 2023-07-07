package com.windry.chordplayer.domain;

import com.windry.chordplayer.spec.Tag;
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

    @OneToMany(mappedBy = "lyrics", cascade = CascadeType.ALL, orphanRemoval = true)
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

    public void updateLyrics(Tag tag, String lyrics, List<Chords> chords) {
        this.tag = tag;
        this.lyrics = lyrics;

        // 코드 업데이트
        for (int i = 0; i < this.chords.size(); ++i) {
            if (i < chords.size()) {
                Chords chords1 = this.chords.get(i);
                Chords chords2 = chords.get(i);
                chords1.updateChord(chords2.getChord());
            } else {
                this.chords.remove(i);
                i--;
            }
        }

        for (int i = this.chords.size(); i < chords.size(); ++i) {
            Chords chords1 = chords.get(i);
            this.chords.add(chords1);
        }
    }
}
