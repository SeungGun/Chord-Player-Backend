package com.windry.chordplayer.exception;

import org.springframework.http.HttpStatus;

public class InvalidInputException extends CustomRuntimeException {
    public InvalidInputException() {
        super(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_PARAMETER);
    }
}
