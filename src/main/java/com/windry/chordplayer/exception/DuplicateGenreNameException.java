package com.windry.chordplayer.exception;

import org.springframework.http.HttpStatus;

public class DuplicateGenreNameException extends CustomRuntimeException {
    public DuplicateGenreNameException() {
        super(HttpStatus.BAD_REQUEST, ErrorCode.DUPLICATE_GENRE_NAME);
    }
}
