package com.windry.chordplayer.dto.genre;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateGenreDto {
    private String name;

    @Builder
    public CreateGenreDto(String name) {
        this.name = name;
    }
}
