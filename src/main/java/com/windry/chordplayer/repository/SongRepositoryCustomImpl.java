package com.windry.chordplayer.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.windry.chordplayer.domain.*;
import com.windry.chordplayer.dto.FiltersOfSongList;
import com.windry.chordplayer.dto.SongListItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.windry.chordplayer.domain.QSong.song;

@RequiredArgsConstructor
public class SongRepositoryCustomImpl implements SongRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<SongListItemDto> searchAllSong(FiltersOfSongList filtersOfSongList, Pageable pageable) {
        List<Song> result = queryFactory
                .selectFrom(song)
                .where(
                        searchByCriteria(filtersOfSongList.getSearchCriteria(),
                                filtersOfSongList.getSearchKeyword()),
                        searchByGender(filtersOfSongList.getGender()),
                        searchByKey(filtersOfSongList.getSearchKey()),
                        searchByGenre(filtersOfSongList.getSearchGenre())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(sortByStrategy(filtersOfSongList.getSortStrategy()))
                .fetch();
        return result.stream().map(r -> {
            List<String> list = r.getSongGenres().stream().map(g -> g.getGenre().getName()).toList();
            return SongListItemDto.builder()
                    .songId(r.getId())
                    .title(r.getTitle())
                    .artist(r.getArtist())
                    .bpm(r.getBpm())
                    .gender(r.getGender())
                    .modulation(r.getModulation())
                    .note(r.getNote())
                    .originalKey(r.getOriginalKey())
                    .genres(list)
                    .build();
        }).toList();
    }

    /**
     * 노래의 키로 검색하기
     *
     * @param key 검색할 key
     * @return 동일한 key 검색
     */
    public Predicate searchByKey(String key) {
        if (key == null)
            return null;

        return song.originalKey.eq(key);
    }

    /**
     * 노래의 성별로 검색하기
     *
     * @param gender 검색할 성별
     * @return 동일한 성별 검색
     */
    public Predicate searchByGender(Gender gender) {
        if (gender == null)
            return null;

        return song.gender.eq(gender);
    }

    /**
     * 노래의 장르로 검색하기 (노래의 장르들 중 포함되는)
     *
     * @param genre 검색할 장르
     * @return 장르 목록 중 포함된 것들 중 검색
     */
    public Predicate searchByGenre(String genre) {
        if (genre == null)
            return null;

        return song.songGenres.any().genre.name.eq(genre); // 리스트 중 어떤 것이라도 장르의 이름과 동일한 것이 있는지 검사
    }

    /**
     * 검색어로 검색하기 (제목, 가수)
     *
     * @param criteria 검색 기준
     * @param keyword  검색할 키워드
     * @return 키워드가 포함되어있는지 검색
     */
    public Predicate searchByCriteria(SearchCriteria criteria, String keyword) {
        if (criteria == null)
            return null;

        if (keyword == null)
            return null;

        switch (criteria) {
            case TITLE -> {
                return song.title.contains(keyword);
            }
            case ARTIST -> {
                return song.artist.contains(keyword);
            }
        }
        return null;
    }

    /**
     * 정렬 전략
     *
     * @param sortStrategy 정렬 전략
     * @return 정렬 구체화
     */
    public OrderSpecifier<?> sortByStrategy(SortStrategy sortStrategy) {
        if (sortStrategy == null)
            return new OrderSpecifier<>(Order.DESC, song.id);

        switch (sortStrategy) {
            case CHRONOLOGICAL -> {
                return new OrderSpecifier<>(Order.DESC, song.id);
            }
            case NAME -> {
                return new OrderSpecifier<>(Order.ASC, song.title);
            }
            case VIEW -> {
                return new OrderSpecifier<>(Order.DESC, song.viewCount);
            }
        }
        return null;
    }
}
