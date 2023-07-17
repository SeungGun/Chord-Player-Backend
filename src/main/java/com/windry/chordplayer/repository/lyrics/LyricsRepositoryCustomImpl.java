package com.windry.chordplayer.repository.lyrics;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.windry.chordplayer.domain.Lyrics;
import com.windry.chordplayer.dto.lyrics.DetailLyricsDto;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.windry.chordplayer.domain.QChords.chords;
import static com.windry.chordplayer.domain.QLyrics.lyrics1;

@RequiredArgsConstructor
public class LyricsRepositoryCustomImpl implements LyricsRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<DetailLyricsDto> getPagingLyricsBySong(Long offset, Long size, Long songId) {
        List<Lyrics> lyrics = queryFactory
                .selectFrom(lyrics1)
                .join(lyrics1.chords, chords).fetchJoin()
                .where(
                        pagination(offset),
                        lyrics1.song.id.eq(songId)
                )
                .limit(size)
                .orderBy(new OrderSpecifier<>(Order.ASC, lyrics1.line))
                .fetch();
        return lyrics.stream().map(l ->
                DetailLyricsDto.builder()
                        .tag(l.getTag())
                        .line(l.getLine())
                        .chords(l.convertStringChords(l.getChords()))
                        .lyrics(l.getLyrics())
                        .build()
        ).toList();
    }

    private Predicate pagination(Long offset) {
        if (offset == null || offset == 0L) {
            return null;
        }
        return lyrics1.line.gt(offset);
    }
}
