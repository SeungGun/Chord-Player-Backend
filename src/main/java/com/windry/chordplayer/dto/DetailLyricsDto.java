package com.windry.chordplayer.dto;

import com.windry.chordplayer.spec.Tag;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class DetailLyricsDto {

    private Integer line;
    private String lyrics;
    private Tag tag;
    private List<String> chords = new ArrayList<>();

    @Builder
    public DetailLyricsDto(Integer line, String lyrics, Tag tag, List<String> chords) {
        this.line = line;
        this.lyrics = lyrics;
        this.tag = tag;
        this.chords = chords;
    }
}
