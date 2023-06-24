package com.windry.chordplayer.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GenreListDto {
    private Long genreId;
    private String genreName;

    @Builder
    public GenreListDto(Long genreId, String genreName) {
        this.genreId = genreId;
        this.genreName = genreName;
    }
}

