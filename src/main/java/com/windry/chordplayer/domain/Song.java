package com.windry.chordplayer.domain;

import com.windry.chordplayer.spec.Gender;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

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

    @OneToMany(mappedBy = "song", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SongGenre> songGenres = new ArrayList<>();

    @OneToMany(mappedBy = "song", cascade = CascadeType.ALL, orphanRemoval = true)
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

    public void changeRequestFields(String title, String artist, String originalKey, Gender gender, Integer bpm, String modulation, String note) {
        this.title = title;
        this.artist = artist;
        this.originalKey = originalKey;
        this.gender = gender;
        this.bpm = bpm;
        this.modulation = modulation;
        this.note = note;
    }

    public void updateLyrics(List<Lyrics> newLyrics) {
        if (newLyrics != null) {
            // 가사 업데이트
            for (int i = 0; i < this.lyricsList.size(); ++i) {
                if (i < newLyrics.size()) {
                    Lyrics exist = this.lyricsList.get(i);
                    Lyrics updated = newLyrics.get(i);

                    exist.updateTag(updated.getTag());
                    exist.updateLyrics(updated.getLyrics());
                    exist.updateChords(updated.getChords());
                } else {
                    this.lyricsList.remove(i);
                    i--;
                }
            }

            for (int i = this.lyricsList.size(); i < newLyrics.size(); ++i) {
                Lyrics lyrics1 = newLyrics.get(i);
                this.lyricsList.add(lyrics1);
            }
        }
    }

    public void updateGenres(List<SongGenre> newGenres) {
        if (newGenres != null) {
            // 장르 업데이트
            for (int i = 0; i < this.songGenres.size(); ++i) {
                if (i < newGenres.size()) {
                    SongGenre exist = this.songGenres.get(i);
                    SongGenre updated = newGenres.get(i);
                    exist.changeSongGenre(updated.getGenre());
                } else {
                    this.songGenres.remove(i); // orphanRemoval 속성!!
                    i--;
                }
            }

            for (int i = this.songGenres.size(); i < newGenres.size(); ++i) {
                SongGenre songGenre = newGenres.get(i);
                this.songGenres.add(songGenre);
            }
        }
    }

    public void addLyrics(Lyrics lyrics) {
        lyricsList.add(lyrics);
        lyrics.changeSong(this);
    }

    public void addGenre(SongGenre songGenre) {
        songGenres.add(songGenre);
    }

    public void updateViewCount() {
        this.viewCount++;
    }
}
