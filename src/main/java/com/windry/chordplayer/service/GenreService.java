package com.windry.chordplayer.service;

import com.windry.chordplayer.domain.Genre;
import com.windry.chordplayer.dto.genre.CreateGenreDto;
import com.windry.chordplayer.dto.genre.GenreListDto;
import com.windry.chordplayer.exception.DuplicateGenreNameException;
import com.windry.chordplayer.exception.InvalidInputException;
import com.windry.chordplayer.exception.NoSuchDataException;
import com.windry.chordplayer.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;

    @Transactional
    public Long createGenre(CreateGenreDto createGenreDto) {
        String name = createGenreDto.getName();
        checkDuplicateGenre(name);

        Genre genre = Genre.builder().name(name.replace(" ", "")).build();

        return genreRepository.save(genre).getId();
    }

    public List<GenreListDto> getGenreList() {
        return genreRepository.findAll()
                .stream()
                .map(g -> GenreListDto.builder()
                        .genreId(g.getId())
                        .genreName(g.getName())
                        .build())
                .toList();
    }

    @Transactional
    public void removeGenre(Long genreId) {
        if (genreRepository.findById(genreId).isEmpty())
            throw new NoSuchDataException();

        genreRepository.deleteById(genreId);
    }

    private void checkDuplicateGenre(String name) {
        if (name == null)
            throw new InvalidInputException();

        Optional<Genre> optional = genreRepository.findGenreByName(name.replace(" ", ""));
        if (optional.isPresent())
            throw new DuplicateGenreNameException();
    }


}
