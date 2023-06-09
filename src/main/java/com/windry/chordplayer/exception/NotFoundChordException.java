package com.windry.chordplayer.exception;

import org.springframework.http.HttpStatus;

public class NotFoundChordException extends CustomRuntimeException {
    public NotFoundChordException() {
        super(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_CHORD_MATCH);
    }
}
