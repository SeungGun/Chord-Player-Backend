package com.windry.chordplayer.exception;

import org.springframework.http.HttpStatus;

public class InvalidKeyException extends CustomRuntimeException {
    public InvalidKeyException() {
        super(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_KEY);
    }
}
