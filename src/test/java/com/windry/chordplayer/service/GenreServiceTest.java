package com.windry.chordplayer.service;

import com.windry.chordplayer.dto.CreateGenreDto;
import com.windry.chordplayer.exception.DuplicateGenreNameException;
import com.windry.chordplayer.repository.GenreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class GenreServiceTest {

    @Autowired
    private GenreService genreService;
    @Autowired
    private GenreRepository genreRepository;

    @Test
    @DisplayName("장르 데이터를 생성할 때, 중복되는 장르 이름이 있을 경우 예외를 발생시킨다.")
    void createGenre() {
        // given
        CreateGenreDto dto1 = CreateGenreDto.builder().name("락").build();
        CreateGenreDto dto2 = CreateGenreDto.builder().name("락").build();

        // when
        Long genreId = genreService.createGenre(dto1);

        // then
        Assertions.assertEquals("락", genreRepository.findById(genreId).get().getName());
        Assertions.assertThrows(DuplicateGenreNameException.class, () -> genreService.createGenre(dto2));
    }

    @Test
    @DisplayName("예외를 고려하여 장르 목록 전체를 조회한다.")
    void getGenreList() {
        // given
        CreateGenreDto dto1 = CreateGenreDto.builder().name("락").build();
        CreateGenreDto dto2 = CreateGenreDto.builder().name("발라드").build();
        CreateGenreDto dto3 = CreateGenreDto.builder().name("락").build();
        CreateGenreDto dto4 = CreateGenreDto.builder().name("재즈").build();
        // when
        try {
            genreService.createGenre(dto1);
        } catch (DuplicateGenreNameException e) {

        }

        try {
            genreService.createGenre(dto2);
        } catch (DuplicateGenreNameException e) {

        }
        try {
            genreService.createGenre(dto3);
        } catch (DuplicateGenreNameException e) {

        }
        try {
            genreService.createGenre(dto4);
        } catch (DuplicateGenreNameException e) {

        }

        // then
        Assertions.assertEquals(3, genreService.getGenreList().size());
    }

    @Test
    @DisplayName("장르 데이터를 삭제가 되면 해당 데이터는 더 이상 조회되지 않는다.")
    void removeGenre() {
        // given
        CreateGenreDto dto1 = CreateGenreDto.builder().name("락").build();

        // when
        Long genre = genreService.createGenre(dto1);
        genreService.removeGenre(genre);

        // then
        assertTrue(genreRepository.findById(genre).isEmpty());
    }
}