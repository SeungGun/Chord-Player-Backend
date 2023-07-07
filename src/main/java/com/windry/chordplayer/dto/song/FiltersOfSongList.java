package com.windry.chordplayer.dto.song;

import com.windry.chordplayer.spec.Gender;
import com.windry.chordplayer.spec.SearchCriteria;
import com.windry.chordplayer.spec.SortStrategy;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FiltersOfSongList {

    private SearchCriteria searchCriteria;
    private SortStrategy sortStrategy;
    private String searchKeyword;
    private Gender gender;
    private String searchKey;
    private String searchGenre;

    @Builder
    public FiltersOfSongList(SearchCriteria searchCriteria, SortStrategy sortStrategy, String searchKeyword, Gender gender, String searchKey, String searchGenre) {
        this.searchCriteria = searchCriteria;
        this.sortStrategy = sortStrategy;
        this.searchKeyword = searchKeyword;
        this.gender = gender;
        this.searchKey = searchKey;
        this.searchGenre = searchGenre;
    }
}
