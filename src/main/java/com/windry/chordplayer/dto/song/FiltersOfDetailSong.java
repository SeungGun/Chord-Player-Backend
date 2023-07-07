package com.windry.chordplayer.dto.song;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.windry.chordplayer.spec.Tuning;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FiltersOfDetailSong {

    private Integer capo;
    private Tuning tuning;
    private Boolean convertGender;
    @JsonProperty("isKeyUp")
    private Boolean isKeyUp;
    private Integer key;

    @Builder
    public FiltersOfDetailSong(Integer capo, Tuning tuning, Boolean convertGender, Boolean isKeyUp, Integer key) {
        this.capo = capo;
        this.tuning = tuning;
        this.convertGender = convertGender;
        this.isKeyUp = isKeyUp;
        this.key = key;
    }
}
