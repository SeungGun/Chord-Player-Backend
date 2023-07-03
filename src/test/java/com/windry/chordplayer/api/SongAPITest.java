package com.windry.chordplayer.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.windry.chordplayer.domain.Genre;
import com.windry.chordplayer.domain.Song;
import com.windry.chordplayer.domain.SongGenre;
import com.windry.chordplayer.dto.CreateSongDto;
import com.windry.chordplayer.dto.LyricsDto;
import com.windry.chordplayer.repository.GenreRepository;
import com.windry.chordplayer.repository.SongRepository;
import com.windry.chordplayer.spec.Gender;
import com.windry.chordplayer.spec.SearchCriteria;
import com.windry.chordplayer.spec.SortStrategy;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class SongAPITest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SongRepository songRepository;
    @Autowired
    private GenreRepository genreRepository;

    @DisplayName("이미 존재하는 제목과 가수의 노래를 생성할 때, 예외를 발생한다.")
    @Test
    void createSongWithException() throws Exception {
        Genre genre = Genre.builder().name("락").build();
        genreRepository.save(genre);

        Song song = new Song();
        SongGenre songGenre = SongGenre.builder()
                .genre(genre)
                .song(song)
                .build();
        List<SongGenre> genres = new ArrayList<>();
        genres.add(songGenre);
        song.changeRequestFields(
                "하늘을 달리다", "이적", "E", Gender.MALE, 116, null, null, genres
        );
        songRepository.save(song);

        List<String> genreList = new ArrayList<>();
        genreList.add("락");

        CreateSongDto dto = CreateSongDto.builder()
                .title("하늘을 달리다")
                .artist("이적")
                .originalKey("E")
                .bpm(116)
                .gender(Gender.MALE)
                .modulation(null)
                .contents(null)
                .genres(genreList)
                .build();
        List<LyricsDto> lyricsDtoList = new ArrayList<>();
        lyricsDtoList.add(LyricsDto.builder()
                .tag("INTRO")
                .lyrics(null)
                .chords(LyricsDto.getAllChords("B", "A"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag("INTRO")
                .lyrics(null)
                .chords(LyricsDto.getAllChords("E", "A"))
                .build());

        lyricsDtoList.add(LyricsDto.builder()
                .tag("INTRO")
                .lyrics(null)
                .chords(LyricsDto.getAllChords("B", "A"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("두근거렸지 난 결국")
                .chords(LyricsDto.getAllChords("E", "Aadd2"))
                .build());
        dto.setContents(lyricsDtoList);

        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);

        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String json = objectWriter.writeValueAsString(dto);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/songs")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError()).andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        System.out.println("response.getContentAsString() = " + response.getContentAsString());
    }

    @DisplayName("제목은 중복되도 가수 이름이 다르면 성공적으로 노래가 생성된다.")
    @Test
    void createSong() throws Exception {
        Genre genre = Genre.builder().name("락").build();
        genreRepository.save(genre);

        Song song = new Song();
        SongGenre songGenre = SongGenre.builder()
                .genre(genre)
                .song(song)
                .build();
        List<SongGenre> genres = new ArrayList<>();
        genres.add(songGenre);
        song.changeRequestFields(
                "하늘을 달리다", "이적", "E", Gender.MALE, 116, null, null, genres
        );
        songRepository.save(song);

        List<String> genreList = new ArrayList<>();
        genreList.add("락");

        CreateSongDto dto = CreateSongDto.builder()
                .title("하늘을 달리다")
                .artist("허각")
                .originalKey("E")
                .bpm(116)
                .gender(Gender.MALE)
                .modulation(null)
                .contents(null)
                .genres(genreList)
                .build();
        List<LyricsDto> lyricsDtoList = new ArrayList<>();
        lyricsDtoList.add(LyricsDto.builder()
                .tag("INTRO")
                .lyrics(null)
                .chords(LyricsDto.getAllChords("B", "A"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag("INTRO")
                .lyrics(null)
                .chords(LyricsDto.getAllChords("E", "A"))
                .build());

        lyricsDtoList.add(LyricsDto.builder()
                .tag("INTRO")
                .lyrics(null)
                .chords(LyricsDto.getAllChords("B", "A"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("두근거렸지 난 결국")
                .chords(LyricsDto.getAllChords("E", "Aadd2"))
                .build());
        dto.setContents(lyricsDtoList);

        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);

        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String json = objectWriter.writeValueAsString(dto);

        this.mockMvc.perform(post("/api/songs")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(document("songs/create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("title").description("노래의 제목"),
                                fieldWithPath("artist").description("노래의 가수"),
                                fieldWithPath("originalKey").description("노래의 원키"),
                                fieldWithPath("gender").description("노래를 부른 가수의 성별"),
                                fieldWithPath("note").description("이 노래에 작성할 메모").optional(),
                                fieldWithPath("bpm").description("노래의 BPM").optional(),
                                fieldWithPath("modulation").description("노래의 전조").optional(),
                                fieldWithPath("genres[]").description("노래의 장르 목록"),
                                fieldWithPath("contents[].lyrics").description("노래의 가사").optional(),
                                fieldWithPath("contents[].tag").description("현재 마디의 상태").optional(),
                                fieldWithPath("contents[].chords[]").description("현재 마디의 코드 목록")
                        ),
                        responseFields(
                                fieldWithPath("songId").description("저장한 노래의 고유 ID")
                        )
                ));
    }


    @DisplayName("노래 목록을 조회할 때, 모든 필터를 적용해본다. ")
    @Test
    public void getSongListWithAllFilters() throws Exception {
        Genre genre = Genre.builder().name("락").build();
        genreRepository.save(genre);

        Song song = new Song();
        SongGenre songGenre = SongGenre.builder()
                .genre(genre)
                .song(song)
                .build();
        List<SongGenre> genres = new ArrayList<>();
        genres.add(songGenre);
        song.changeRequestFields(
                "하늘을 달리다", "이적", "E", Gender.MALE, 116, null, null, genres
        );
        Long id = songRepository.save(song).getId();

        MvcResult mvcResult = this.mockMvc.perform(get("/api/songs")
                        .param("page", id.toString())
                        .param("size", "10")
                        .param("searchCriteria", "TITLE")
                        .param("keyword", "하늘")
                        .param("gender", "MALE")
                        .param("key", "E")
                        .param("genre", "락")
                ).andExpect(status().isOk())
                .andDo(document("songs/get-all",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("page").description("페이지 시작 위치에 대한 노래 ID"),
                                parameterWithName("size").description("가져올 목록의 개수"),
                                parameterWithName("searchCriteria").description("검색 기준(제목, 가수)").optional(),
                                parameterWithName("keyword").description("검색하고자 하는 키워드").optional(),
                                parameterWithName("gender").description("검색하고자 하는 성별").optional(),
                                parameterWithName("key").description("검색하고자 하는 노래의 원키").optional(),
                                parameterWithName("genre").description("검색하고자 하는 노래의 장르").optional(),
                                parameterWithName("sort").description("목록의 정렬 기준").optional()
                        ),
                        responseFields(
                                fieldWithPath("[]").description("노래 목록"),
                                fieldWithPath("[].songId").description("노래의 고유 ID"),
                                fieldWithPath("[].title").description("노래의 제목"),
                                fieldWithPath("[].artist").description("노래의 가수"),
                                fieldWithPath("[].originalKey").description("노래의 원키"),
                                fieldWithPath("[].bpm").description("노래의 BPM"),
                                fieldWithPath("[].modulation").description("노래의 전조"),
                                fieldWithPath("[].note").description("노래의 노트"),
                                fieldWithPath("[].gender").description("노래의 성별"),
                                fieldWithPath("[].genres[]").description("노래의 장르")
                        )
                ))
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        String content = response.getContentAsString();

        JSONArray jsonArray = new JSONArray(content);

        assertTrue(jsonArray.length() > 0);
    }

    @DisplayName("노래 목록을 조회할 때 커서에 해당하는 데이터가 존재하지 않으면 예외를 발생한다.")
    @Test
    public void getSongListWithNotExistData() throws Exception {
        this.mockMvc.perform(get("/api/songs")
                .param("page", "3")
                .param("size", "10")
                .param("searchCriteria", SearchCriteria.TITLE.name())
                .param("keyword", "하늘")
                .param("gender", Gender.MALE.name())
                .param("key", "E")
                .param("sort", SortStrategy.CHRONOLOGICAL.name())
                .param("genre", "락")
        ).andExpect(status().is4xxClientError()).andReturn();
    }

    @DisplayName("상세 노래 조회에서 여러 키 변경 필터를 적용해본다.")
    @Test
    void getDetailSongWithKeyChange() throws Exception {
        List<String> genreList = new ArrayList<>();
        genreList.add("락");

        CreateSongDto dto = CreateSongDto.builder()
                .title("하늘을 달리다")
                .artist("허각")
                .originalKey("E")
                .bpm(116)
                .gender(Gender.MALE)
                .modulation(null)
                .contents(null)
                .genres(genreList)
                .build();
        List<LyricsDto> lyricsDtoList = new ArrayList<>();
        lyricsDtoList.add(LyricsDto.builder()
                .tag("INTRO")
                .lyrics(null)
                .chords(LyricsDto.getAllChords("B", "A"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag("INTRO")
                .lyrics(null)
                .chords(LyricsDto.getAllChords("E", "A"))
                .build());

        lyricsDtoList.add(LyricsDto.builder()
                .tag("INTRO")
                .lyrics(null)
                .chords(LyricsDto.getAllChords("B", "A"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("두근거렸지 난 결국")
                .chords(LyricsDto.getAllChords("E", "Aadd2"))
                .build());
        dto.setContents(lyricsDtoList);

        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);

        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String json = objectWriter.writeValueAsString(dto);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/songs")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        JSONObject jsonObject = new JSONObject(response.getContentAsString());

        MvcResult result = this.mockMvc.perform(get("/api/songs/{songId}", jsonObject.optLong("songId"))
                        .param("currentKey", "E")
                        .param("key-up", "true")
                        .param("key", "2")
                        .param("gender", "true")
                        .param("offset", "0")
                        .param("size", "10")
                )
                .andExpect(status().isOk())
                .andDo(document("songs/get-detail",
                        pathParameters(
                                parameterWithName("songId").description("노래의 고유 ID")
                        ),
                        queryParameters(
                                parameterWithName("currentKey").description("현재 마디에서의 노래 키"),
                                parameterWithName("offset").description("마지막 가사 마디의 위치"),
                                parameterWithName("size").description("가져올 가사의 마디 개수"),
                                parameterWithName("capo").description("기타 카포 적용 크기").optional(),
                                parameterWithName("tuning").description("기타 튜닝의 종류").optional(),
                                parameterWithName("gender").description("남/여 키 변경 여부").optional(),
                                parameterWithName("key-up").description("키 올림 여부").optional(),
                                parameterWithName("key").description("키 변경할 크기").optional()
                        ),
                        responseFields(
                                fieldWithPath("title").description("노래의 제목"),
                                fieldWithPath("artist").description("노래의 가수"),
                                fieldWithPath("currentKey").description("현재 노래의 키"),
                                fieldWithPath("gender").description("현재 노래의 성별"),
                                fieldWithPath("contents[].line").description("현재 노래의 마디"),
                                fieldWithPath("contents[].lyrics").description("현재 마디의 가사").optional(),
                                fieldWithPath("contents[].tag").description("현재 마디의 태그(상태)").optional(),
                                fieldWithPath("contents[].chords[]").description("현재 마디의 코드 목록")
                        )

                ))
                .andReturn();

        JSONObject jsonObject2 = new JSONObject(result.getResponse().getContentAsString());

        Assertions.assertEquals("F#", jsonObject2.optJSONArray("contents").getJSONObject(0).getJSONArray("chords").get(0));
    }

    @DisplayName("노래의 가사를 수정하면 성공적으로 수정이 되야한다.")
    @Test
    void modifySongForLyrics() throws Exception {
        List<String> genreList = new ArrayList<>();
        genreList.add("락");

        CreateSongDto dto = CreateSongDto.builder()
                .title("하늘을 달리다")
                .artist("허각")
                .originalKey("E")
                .bpm(116)
                .gender(Gender.MALE)
                .modulation(null)
                .contents(null)
                .genres(genreList)
                .build();
        List<LyricsDto> lyricsDtoList = new ArrayList<>();
        lyricsDtoList.add(LyricsDto.builder()
                .tag("INTRO")
                .lyrics(null)
                .chords(LyricsDto.getAllChords("B", "A"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag("INTRO")
                .lyrics(null)
                .chords(LyricsDto.getAllChords("E", "A"))
                .build());

        lyricsDtoList.add(LyricsDto.builder()
                .tag("INTRO")
                .lyrics(null)
                .chords(LyricsDto.getAllChords("B", "A"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("두근거렸지 난 결국")
                .chords(LyricsDto.getAllChords("E", "Aadd2"))
                .build());
        dto.setContents(lyricsDtoList);

        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);

        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String json = objectWriter.writeValueAsString(dto);

        MvcResult mvcResult = this.mockMvc.perform(post("/api/songs")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        JSONObject jsonObject = new JSONObject(response.getContentAsString());

        dto.getContents().get(dto.getContents().size() - 1).setLyrics("국결 난 지렸거근두");
        String modifyJson = objectWriter.writeValueAsString(dto);
        this.mockMvc.perform(put("/api/songs/{songId}", jsonObject.optLong("songId"))
                        .content(modifyJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("songs/modify",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("songId").description("노래의 고유 ID")
                        ),
                        requestFields(
                                fieldWithPath("title").description("노래의 제목"),
                                fieldWithPath("artist").description("노래의 가수"),
                                fieldWithPath("originalKey").description("노래의 원키"),
                                fieldWithPath("gender").description("노래를 부른 가수의 성별"),
                                fieldWithPath("note").description("이 노래에 작성할 메모").optional(),
                                fieldWithPath("bpm").description("노래의 BPM").optional(),
                                fieldWithPath("modulation").description("노래의 전조").optional(),
                                fieldWithPath("genres[]").description("노래의 장르 목록"),
                                fieldWithPath("contents[].lyrics").description("노래의 가사").optional(),
                                fieldWithPath("contents[].tag").description("현재 마디의 상태").optional(),
                                fieldWithPath("contents[].chords[]").description("현재 마디의 코드 목록")
                        )
                ));
    }

    @DisplayName("존재하지 않는 노래에 대해 수정을 시도하는 경우 예외가 발생한다.")
    @Test
    void modifySongNotExist() throws Exception {
        Genre genre = Genre.builder().name("락").build();
        genreRepository.save(genre);

        Song song = new Song();
        SongGenre songGenre = SongGenre.builder()
                .genre(genre)
                .song(song)
                .build();
        List<SongGenre> genres = new ArrayList<>();
        genres.add(songGenre);
        song.changeRequestFields(
                "하늘을 달리다", "이적", "E", Gender.MALE, 116, null, null, genres
        );
        Long songId = songRepository.save(song).getId();

        List<String> genreList = new ArrayList<>();
        genreList.add("락");

        CreateSongDto dto = CreateSongDto.builder()
                .title("달렸다 하늘을")
                .artist("각허")
                .originalKey("E")
                .bpm(116)
                .gender(Gender.MALE)
                .modulation(null)
                .contents(null)
                .genres(genreList)
                .build();

        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);

        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String modifyJson = objectWriter.writeValueAsString(dto);
        this.mockMvc.perform(put("/api/songs/" + (songId + 1))
                        .content(modifyJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("존재하는 노래를 삭제하면 성공적으로 삭제가 되야한다.")
    @Test
    void deleteSong() throws Exception {
        Genre genre = Genre.builder().name("락").build();
        genreRepository.save(genre);

        Song song = new Song();
        SongGenre songGenre = SongGenre.builder()
                .genre(genre)
                .song(song)
                .build();
        List<SongGenre> genres = new ArrayList<>();
        genres.add(songGenre);
        song.changeRequestFields(
                "하늘을 달리다", "이적", "E", Gender.MALE, 116, null, null, genres
        );
        Long songId = songRepository.save(song).getId();
        this.mockMvc.perform(delete("/api/songs/{songId}", songId))
                .andExpect(status().isOk())
                .andDo(document("songs/delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("songId").description("노래의 고유 ID")
                        ))
                )
        ;
    }

    @DisplayName("존재하지 않는 노래에 대해 삭제를 시도하는 경우 예외가 발생한다.")
    @Test
    void deleteSongNotExist() throws Exception {
        Genre genre = Genre.builder().name("락").build();
        genreRepository.save(genre);

        Song song = new Song();
        SongGenre songGenre = SongGenre.builder()
                .genre(genre)
                .song(song)
                .build();
        List<SongGenre> genres = new ArrayList<>();
        genres.add(songGenre);
        song.changeRequestFields(
                "하늘을 달리다", "이적", "E", Gender.MALE, 116, null, null, genres
        );
        Long songId = songRepository.save(song).getId();
        this.mockMvc.perform(delete("/api/songs/" + (songId + 1)))
                .andExpect(status().is4xxClientError());
    }
}