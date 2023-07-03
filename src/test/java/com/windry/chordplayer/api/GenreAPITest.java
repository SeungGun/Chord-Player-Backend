package com.windry.chordplayer.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.windry.chordplayer.domain.Genre;
import com.windry.chordplayer.dto.genre.CreateGenreDto;
import com.windry.chordplayer.repository.GenreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@AutoConfigureRestDocs
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
                .andExpect(status().isCreated())
                .andDo(document("genres/create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("name").description("장르 이름")
                        ),
                        responseFields(
                                fieldWithPath("genreId").description("장르의 고유 ID")
                        )
                ));
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

        this.mockMvc.perform(delete("/api/genres/{genreId}", genreId))
                .andExpect(status().isOk())
                .andDo(document("genres/delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("genreId").description("장르의 고유 ID")
                        )
                ));
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
        Genre genre = Genre.builder().name("락").build();
        genreRepository.save(genre);
        Genre genre2 = Genre.builder().name("발라드").build();
        genreRepository.save(genre2);
        Genre genre3 = Genre.builder().name("재즈").build();
        genreRepository.save(genre3);

        this.mockMvc.perform(get("/api/genres"))
                .andExpect(status().isOk())
                .andDo(document("genres/get-all",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[]").description("장르 목록"),
                                fieldWithPath("[].genreId").description("장르의 고유 ID"),
                                fieldWithPath("[].genreName").description("장르 이름")
                        )));
    }

}