package com.windry.chordplayer.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.windry.chordplayer.domain.Genre;
import com.windry.chordplayer.domain.Song;
import com.windry.chordplayer.domain.SongGenre;
import com.windry.chordplayer.dto.song.CreateSongDto;
import com.windry.chordplayer.dto.lyrics.LyricsDto;
import com.windry.chordplayer.repository.GenreRepository;
import com.windry.chordplayer.repository.song.SongRepository;
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
                "하늘을 달리다", "이적", "E", Gender.MALE, 116, null, null
        );
        song.updateLyrics(null);
        song.updateGenres(genres);

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
        Genre genre = Genre.builder().name("발라드").build();
        genreRepository.save(genre);

        Song song = new Song();
        SongGenre songGenre = SongGenre.builder()
                .genre(genre)
                .song(song)
                .build();
        List<SongGenre> genres = new ArrayList<>();
        genres.add(songGenre);
        song.changeRequestFields(
                "다정히 내 이름을 부르면", "경서예지", "Db", Gender.FEMALE, 72, null, null
        );
        song.updateLyrics(null);
        song.updateGenres(genres);

        songRepository.save(song);

        List<String> genreList = new ArrayList<>();
        genreList.add("발라드");

        CreateSongDto dto = CreateSongDto.builder()
                .title("다정히 내 이름을 부르면")
                .artist("경서예지, 전건호")
                .originalKey("Db")
                .bpm(72)
                .gender(Gender.MIXED)
                .modulation("Db-Eb-F-G")
                .contents(null)
                .genres(genreList)
                .note("이 노래는 두키씩 키 변경 일어남.")
                .build();

        List<LyricsDto> lyricsDtoList = new ArrayList<>();
        lyricsDtoList.add(LyricsDto.builder()
                .tag("INTRO")
                .lyrics(null)
                .chords(LyricsDto.getAllChords("Db", "Ab"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag("INTRO")
                .lyrics(null)
                .chords(LyricsDto.getAllChords("Bbm7", "Fm7"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag("INTRO")
                .lyrics(null)
                .chords(LyricsDto.getAllChords("Gb", "Gbm6"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("끝 없")
                .chords(LyricsDto.getAllChords("Db", "Dbsus4", "Db"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("이 별빛이 내리던 밤")
                .chords(LyricsDto.getAllChords("Db", "Ab"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("기분 좋은 바람이")
                .chords(LyricsDto.getAllChords("Bbm7", "Db/Ab"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("두 뺨을 스치고")
                .chords(LyricsDto.getAllChords("Gb", "Db/F"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("새벽 바다 한 곳을 보")
                .chords(LyricsDto.getAllChords("Ebm", "Gb/Ab", "Ab"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("는 아름다운 너와 나")
                .chords(LyricsDto.getAllChords("Db", "Ab"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("그림을 그려갔어")
                .chords(LyricsDto.getAllChords("Bbm", "Bbm7/Ab"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("모래 위 떨린 손")
                .chords(LyricsDto.getAllChords("Gbm", "Gbm6"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("끝으로 날 향")
                .chords(LyricsDto.getAllChords("Db", "Fm/C"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("해 웃어주는 입술")
                .chords(LyricsDto.getAllChords("Bbm", "BbmM7/A"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("사랑스러운 두눈을 가진 네")
                .chords(LyricsDto.getAllChords("Ebm", "Db/F", "Gb", "Eb7/G"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag("MODULATION")
                .lyrics("가 다정히 내 이름을")
                .chords(LyricsDto.getAllChords("Ab", "Bb"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("부르면 내 마음이")
                .chords(LyricsDto.getAllChords("Eb", "Bb"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("녹아내려 언제나")
                .chords(LyricsDto.getAllChords("Cm", "Cm7/Bb"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("나 하날 위해 준비된")
                .chords(LyricsDto.getAllChords("Ab", "Eb/G"))
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
                                fieldWithPath("gender").description("노래를 부른 가수의 성별(MALE, FEMALE, MIXED)"),
                                fieldWithPath("note").description("이 노래에 작성할 메모(option)").optional(),
                                fieldWithPath("bpm").description("노래의 BPM(option)").optional(),
                                fieldWithPath("modulation").description("노래의 전조(option)").optional(),
                                fieldWithPath("genres[]").description("노래의 장르 목록"),
                                fieldWithPath("contents[].lyrics").description("노래의 가사(option)").optional(),
                                fieldWithPath("contents[].tag").description("현재 마디의 상태[INTRO, INTERLUDE, MODULATION, BRIDGE, OUTRO](option)").optional(),
                                fieldWithPath("contents[].chords[]").description("현재 마디의 코드 목록")
                        ),
                        responseFields(
                                fieldWithPath("songId").description("저장한 노래의 고유 ID")
                        )
                ));
    }

    @DisplayName("노래 생성 시, contents의 코드가 유효한 루트 코드가 아니면 예외를 발생한다.")
    @Test
    void createSongWithInvalidChord() throws Exception {
        Genre genre = Genre.builder().name("락").build();
        genreRepository.save(genre);

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
                .chords(LyricsDto.getAllChords("XYZ", "PLAO"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag("INTRO")
                .lyrics(null)
                .chords(LyricsDto.getAllChords("E", "A"))
                .build());
        dto.setContents(lyricsDtoList);

        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);

        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String json = objectWriter.writeValueAsString(dto);

        this.mockMvc.perform(post("/api/songs")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

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
                "하늘을 달리다", "이적", "E", Gender.MALE, 116, null, null
        );
        song.updateLyrics(null);
        song.updateGenres(genres);

        Long id = songRepository.save(song).getId();

        MvcResult mvcResult = this.mockMvc.perform(get("/api/songs")
                        .param("page", "0")
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
                                parameterWithName("searchCriteria").description("검색 기준[TITLE, ARTIST](option)").optional(),
                                parameterWithName("keyword").description("검색하고자 하는 키워드(option)").optional(),
                                parameterWithName("gender").description("검색하고자 하는 성별[MALE, FEMALE, MIXED](option)").optional(),
                                parameterWithName("key").description("검색하고자 하는 노래의 원키(option)").optional(),
                                parameterWithName("genre").description("검색하고자 하는 노래의 장르(option)").optional(),
                                parameterWithName("sort").description("목록의 정렬 기준[CHRONOLOGICAL, NAME, VIEW](option)").optional()
                        ),
                        responseFields(
                                fieldWithPath("[]").description("노래 목록"),
                                fieldWithPath("[].songId").description("노래의 고유 ID"),
                                fieldWithPath("[].title").description("노래의 제목"),
                                fieldWithPath("[].artist").description("노래의 가수"),
                                fieldWithPath("[].originalKey").description("노래의 원키"),
                                fieldWithPath("[].bpm").description("노래의 BPM(option)"),
                                fieldWithPath("[].modulation").description("노래의 전조(option)"),
                                fieldWithPath("[].note").description("노래의 노트(option)"),
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

    @DisplayName("노래 목록을 조회할 때, 선택 옵션에 대해 모두 null 값을 부여할 시, 필터링 없이 조회가 된다.")
    @Test
    void getSongListWithNoneFilter() throws Exception {

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
                "하늘을 달리다", "이적", "E", Gender.MALE, 116, null, null
        );
        song.updateLyrics(null);
        song.updateGenres(genres);

        Long id = songRepository.save(song).getId();

        MvcResult mvcResult = this.mockMvc.perform(get("/api/songs")
                .param("page", "0")
                .param("size", "10")
                .param("searchCriteria", "null")
                .param("keyword", "null")
                .param("gender", "null")
                .param("key", "null")
                .param("sort", "null")
                .param("genre", "null")
        ).andExpect(status().isOk()).andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        String content = response.getContentAsString();

        JSONArray jsonArray = new JSONArray(content);

        assertTrue(jsonArray.length() > 0);
    }

    @DisplayName("상세 노래 조회에서 여러 키 변경 필터를 적용해본다.")
    @Test
    void getDetailSongWithKeyChange() throws Exception {
        Genre genre = Genre.builder().name("락").build();
        genreRepository.save(genre);

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
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("songId").description("노래의 고유 ID")
                        ),
                        queryParameters(
                                parameterWithName("currentKey").description("현재 마디에서의 노래 키"),
                                parameterWithName("offset").description("마지막 가사 마디의 위치"),
                                parameterWithName("size").description("가져올 가사의 마디 개수"),
                                parameterWithName("capo").description("기타 카포 적용 크기(option)").optional(),
                                parameterWithName("tuning").description("기타 튜닝의 종류[STANDARD, HALF_STEP, WHOLE_STEP](option)").optional(),
                                parameterWithName("gender").description("남/여 키 변경 여부(option)").optional(),
                                parameterWithName("key-up").description("키 올림 여부(option)").optional(),
                                parameterWithName("key").description("키 변경할 크기(option)").optional()
                        ),
                        responseFields(
                                fieldWithPath("title").description("노래의 제목"),
                                fieldWithPath("artist").description("노래의 가수"),
                                fieldWithPath("currentKey").description("현재 노래의 키"),
                                fieldWithPath("gender").description("현재 노래의 성별"),
                                fieldWithPath("contents[].line").description("현재 노래의 마디"),
                                fieldWithPath("contents[].lyrics").description("현재 마디의 가사(option)").optional(),
                                fieldWithPath("contents[].tag").description("현재 마디의 태그(상태)(option)").optional(),
                                fieldWithPath("contents[].chords[]").description("현재 마디의 코드 목록")
                        )

                ))
                .andReturn();

        JSONObject jsonObject2 = new JSONObject(result.getResponse().getContentAsString());
        Assertions.assertEquals("F#", jsonObject2.optJSONArray("contents").getJSONObject(0).getJSONArray("chords").get(0));
    }

    @DisplayName("상세 노래 조회에서 가사의 페이징이 마디 별로 조회되어야한다.")
    @Test
    void getDetailSongInPaging() throws Exception {
        Genre genre = Genre.builder().name("락").build();
        genreRepository.save(genre);

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

        Long songId = jsonObject.optLong("songId");

        /*
            첫번째 페이징
         */
        MvcResult result = this.mockMvc.perform(get("/api/songs/{songId}", songId)
                        .param("currentKey", "E")
                        .param("gender", "null")
                        .param("key-up", "null")
                        .param("key", "null")
                        .param("capo", "null")
                        .param("offset", "0")
                        .param("size", "3")
                )
                .andExpect(status().isOk())
                .andReturn();

        JSONObject firstJson = new JSONObject(result.getResponse().getContentAsString());
        JSONArray firstArray = firstJson.optJSONArray("contents");

        Assertions.assertEquals(3, firstArray.length());
        // 가져온 리스트 중 마지막 마디의 첫번째 코드 값 비교
        Assertions.assertEquals("B", firstArray.optJSONObject(firstArray.length() - 1).getJSONArray("chords").get(0));

        /*
            두번째 페이징
         */
        MvcResult result2 = this.mockMvc.perform(get("/api/songs/{songId}", songId)
                        .param("currentKey", "E")
                        .param("gender", "false")
                        .param("key-up", "true")
                        .param("key", "1")
                        .param("capo", "0")
                        .param("offset", firstArray.optJSONObject(firstArray.length() - 1).optString("line")) // 마지막으로 가져온 마디 값을 다음 요청의 offset 으로 설정
                        .param("size", "3")
                )
                .andExpect(status().isOk())
                .andReturn();

        JSONObject secondJson = new JSONObject(result2.getResponse().getContentAsString());
        JSONArray secondArray = secondJson.optJSONArray("contents");

        // 가져온 리스트 중 마지막 마디의 두번째 코드 값이 키업이 되었는지 비교
        Assertions.assertEquals("A#add2", secondArray.optJSONObject(secondArray.length() - 1).getJSONArray("chords").get(1));
    }

    @DisplayName("상세 노래 조회에서 현재 키 값이 유효한 키 값이 아니면 예외를 발생한다.")
    @Test
    void getDetailSongAsValidCurrentKey() throws Exception {
        Genre genre = Genre.builder().name("락").build();
        genreRepository.save(genre);

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

        Long songId = jsonObject.optLong("songId");

        /*
            첫번째 페이징
         */
        this.mockMvc.perform(get("/api/songs/{songId}", songId)
                        .param("currentKey", "NOOOO!!")
                        .param("offset", "0")
                        .param("size", "3")
                )
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("노래의 가사를 수정하면 성공적으로 수정이 되야한다.")
    @Test
    void modifySongForLyrics() throws Exception {
        Genre genre = Genre.builder().name("발라드").build();
        genreRepository.save(genre);

        List<String> genreList = new ArrayList<>();
        genreList.add("발라드");

        CreateSongDto dto = CreateSongDto.builder()
                .title("다정히 내 이름을 부르면")
                .artist("경서예지")
                .originalKey("Db")
                .bpm(72)
                .gender(Gender.MIXED)
                .modulation("Db-Eb-F-G")
                .contents(null)
                .genres(genreList)
                .note("이 노래는 두키씩 키 변경 일어남.")
                .build();

        List<LyricsDto> lyricsDtoList = new ArrayList<>();
        lyricsDtoList.add(LyricsDto.builder()
                .tag("INTRO")
                .lyrics(null)
                .chords(LyricsDto.getAllChords("Db", "Ab"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag("INTRO")
                .lyrics(null)
                .chords(LyricsDto.getAllChords("Bbm7", "Fm7"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag("INTRO")
                .lyrics(null)
                .chords(LyricsDto.getAllChords("Gb", "Gbm6"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("끝 없")
                .chords(LyricsDto.getAllChords("Db", "Dbsus4", "Db"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("이 별빛이 내리던 밤")
                .chords(LyricsDto.getAllChords("Db", "Ab"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("기분 좋은 바람이")
                .chords(LyricsDto.getAllChords("Bbm7", "Db/Ab"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("두 뺨을 스치고")
                .chords(LyricsDto.getAllChords("Gb", "Db/F"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("새벽 바다 한 곳을 보")
                .chords(LyricsDto.getAllChords("Ebm", "Gb/Ab", "Ab"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("는 아름다운 너와 나")
                .chords(LyricsDto.getAllChords("Db", "Ab"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("그림을 그려갔어")
                .chords(LyricsDto.getAllChords("Bbm", "Bbm7/Ab"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("모래 위 떨린 손")
                .chords(LyricsDto.getAllChords("Gbm", "Gbm6"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("끝으로 날 향")
                .chords(LyricsDto.getAllChords("Db", "Fm/C"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("해 웃어주는 입술")
                .chords(LyricsDto.getAllChords("Bbm", "BbmM7/A"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("사랑스러운 두눈을 가진 네")
                .chords(LyricsDto.getAllChords("Ebm", "Db/F", "Gb", "Eb7/G"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag("MODULATION")
                .lyrics("가 다정히 내 이름을")
                .chords(LyricsDto.getAllChords("Ab", "Bb"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("부르면 내 마음이")
                .chords(LyricsDto.getAllChords("Eb", "Bb"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("녹아내려 언제나")
                .chords(LyricsDto.getAllChords("Cm", "Cm7/Bb"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag(null)
                .lyrics("나 하날 위해 준비된")
                .chords(LyricsDto.getAllChords("Ab", "Eb/G"))
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

        dto.getContents().get(dto.getContents().size() - 1).setLyrics("된비준 해위 날하 나");
        List<String> chords = new ArrayList<>();
        chords.add("Cm/F#");
        chords.add("C#sus4/Bb");

        dto.getContents().get(dto.getContents().size() - 2).setChords(chords);
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
                                fieldWithPath("gender").description("노래를 부른 가수의 성별[MALE, FEMALE, MIXED]"),
                                fieldWithPath("note").description("이 노래에 작성할 메모(option)").optional(),
                                fieldWithPath("bpm").description("노래의 BPM(option)").optional(),
                                fieldWithPath("modulation").description("노래의 전조(option)").optional(),
                                fieldWithPath("genres[]").description("노래의 장르 목록"),
                                fieldWithPath("contents[].lyrics").description("노래의 가사(option)").optional(),
                                fieldWithPath("contents[].tag").description("현재 마디의 상태[INTRO, INTERLUDE, MODULATION, BRIDGE, OUTRO](option)").optional(),
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
                "하늘을 달리다", "이적", "E", Gender.MALE, 116, null, null
        );
        song.updateLyrics(null);
        song.updateGenres(genres);

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

    @DisplayName("유효하지 않은 루트코드에 대해 수정을 시도하는 경우 예외가 발생한다.")
    @Test
    void modifySongInvalidChord() throws Exception {
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
                "하늘을 달리다", "이적", "E", Gender.MALE, 116, null, null
        );
        song.updateLyrics(null);
        song.updateGenres(genres);

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
        List<LyricsDto> lyricsDtoList = new ArrayList<>();
        lyricsDtoList.add(LyricsDto.builder()
                .tag("INTRO")
                .lyrics(null)
                .chords(LyricsDto.getAllChords("XYZ", "PLAO"))
                .build());
        lyricsDtoList.add(LyricsDto.builder()
                .tag("INTRO")
                .lyrics(null)
                .chords(LyricsDto.getAllChords("KK", "INC"))
                .build());
        dto.setContents(lyricsDtoList);

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
                "하늘을 달리다", "이적", "E", Gender.MALE, 116, null, null
        );
        song.updateLyrics(null);
        song.updateGenres(genres);

        Long songId = songRepository.save(song).getId();
        this.mockMvc.perform(delete("/api/songs/{songId}", songId))
                .andExpect(status().isOk())
                .andDo(document("songs/delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("songId").description("노래의 고유 ID")
                        ))
                );
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
                "하늘을 달리다", "이적", "E", Gender.MALE, 116, null, null
        );
        song.updateLyrics(null);
        song.updateGenres(genres);

        Long songId = songRepository.save(song).getId();
        this.mockMvc.perform(delete("/api/songs/" + (songId + 1)))
                .andExpect(status().is4xxClientError());
    }
}