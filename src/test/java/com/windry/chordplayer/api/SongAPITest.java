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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
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

        MvcResult mvcResult = this.mockMvc.perform(post("/api/songs")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        System.out.println("response.getContentAsString() = " + response.getContentAsString());
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
        ).andExpect(status().isOk()).andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        String content = response.getContentAsString();
        System.out.println("content = " + content);

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
        long songId = Long.parseLong(response.getContentAsString());

        MvcResult result = this.mockMvc.perform(get("/api/songs/" + songId)
                        .param("currentKey", "E")
                        .param("key-up", "true")
                        .param("key", "2")
                        .param("gender", "true")
                )
                .andExpect(status().isOk()).andReturn();

        JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());

        Assertions.assertEquals("F#", jsonObject.optJSONArray("contents").getJSONObject(0).getJSONArray("chords").get(0));
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
                .andExpect(status().isCreated()).andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        long songId = Long.parseLong(response.getContentAsString());

        dto.getContents().get(dto.getContents().size() - 1).setLyrics("국결 난 지렸거근두");
        String modifyJson = objectWriter.writeValueAsString(dto);
        this.mockMvc.perform(put("/api/songs/" + songId)
                        .content(modifyJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
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
        this.mockMvc.perform(delete("/api/songs/" + songId))
                .andExpect(status().isOk());
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