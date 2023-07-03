package com.windry.chordplayer.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.windry.chordplayer.domain.Genre;
import com.windry.chordplayer.dto.CreateGenreDto;
import com.windry.chordplayer.repository.GenreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class GenreAPITest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private GenreRepository genreRepository;

    @DisplayName("장르 데이터를 저장한다.")
    @Test
    void createGenre() throws Exception {
        CreateGenreDto dto = CreateGenreDto.builder()
                .name("발라드").build();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);

        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String json = objectWriter.writeValueAsString(dto);

        this.mockMvc.perform(post("/api/genres")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

    }

    @DisplayName("이미 존재하는 장르 이름으로 생성하는 경우 예외가 발생한다.")
    @Test
    void duplicateGenreName() throws Exception {
        Genre genre = Genre.builder().name("발라드").build();
        genreRepository.save(genre);

        CreateGenreDto dto = CreateGenreDto.builder()
                .name("발라드").build();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);

        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String json = objectWriter.writeValueAsString(dto);

        this.mockMvc.perform(post("/api/genres")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

    }

    @DisplayName("장르 데이터를 삭제한다.")
    @Test
    void deleteGenre() throws Exception {
        Genre genre = Genre.builder().name("발라드").build();
        Long genreId = genreRepository.save(genre).getId();

        this.mockMvc.perform(delete("/api/genres/" + genreId))
                .andExpect(status().isOk());
    }

    @DisplayName("존재하지 않는 장르 데이터에 대해 삭제 시도 시, 예외가 발생한다.")
    @Test
    void deleteGenreNotExist() throws Exception {
        Genre genre = Genre.builder().name("발라드").build();
        Long genreId = genreRepository.save(genre).getId();

        this.mockMvc.perform(delete("/api/genres/" + (genreId + 1)))
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("장르 전체 목록을 조회한다.")
    @Test
    void getGenreList() throws Exception {
        this.mockMvc.perform(get("/api/genres"))
                .andExpect(status().isOk());
    }

}