package com.windry.chordplayer.dto;

import com.windry.chordplayer.domain.Gender;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class CreateSongDto {

    private String title;
    private String artist;
    private String originalKey;
    private Gender gender;
    private int bpm;
    private String modulation;
    private List<LyricsDto> contents = new ArrayList<>();

    @Builder
    public CreateSongDto(String title, String artist, String originalKey, Gender gender, int bpm, String modulation, List<LyricsDto> contents) {
        this.title = title;
        this.artist = artist;
        this.originalKey = originalKey;
        this.gender = gender;
        this.bpm = bpm;
        this.modulation = modulation;
        this.contents = contents;
    }
}