package com.windry.chordplayer.dto;

import com.windry.chordplayer.spec.Gender;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SongListItemDto {
    private Long songId;
    private String title;
    private String artist;
    private String originalKey;
    private int bpm;
    private String modulation;
    private String note;
    private Gender gender;
    private List<String> genres;

    @Builder
    public SongListItemDto(Long songId, String title, String artist, String originalKey, int bpm, String modulation, String note, Gender gender, List<String> genres) {
        this.songId = songId;
        this.title = title;
        this.artist = artist;
        this.originalKey = originalKey;
        this.bpm = bpm;
        this.modulation = modulation;
        this.note = note;
        this.gender = gender;
        this.genres = genres;
    }
}
