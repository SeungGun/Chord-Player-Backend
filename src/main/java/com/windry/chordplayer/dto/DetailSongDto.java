package com.windry.chordplayer.dto;

import com.windry.chordplayer.spec.Gender;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class DetailSongDto {

    private String title;
    private String artist;
    private String currentKey;
    private Gender gender;
    private List<DetailLyricsDto> contents = new ArrayList<>();

    @Builder
    public DetailSongDto(String title, String artist, String currentKey, Gender gender, List<DetailLyricsDto> contents) {
        this.title = title;
        this.artist = artist;
        this.currentKey = currentKey;
        this.gender = gender;
        this.contents = contents;
    }
}
