package com.windry.chordplayer.dto.lyrics;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
public class LyricsDto {

    private String lyrics;
    private String tag;
    private List<String> chords = new ArrayList<>();

    @Builder
    public LyricsDto(String lyrics, String tag, List<String> chords) {
        this.lyrics = lyrics;
        this.tag = tag;
        this.chords = chords;
    }

    public void addAllChords(String ...chords){
        this.chords.addAll(Arrays.asList(chords));
    }

    public static List<String> getAllChords(String ...chords){
        return new ArrayList<>(Arrays.asList(chords));
    }
}
