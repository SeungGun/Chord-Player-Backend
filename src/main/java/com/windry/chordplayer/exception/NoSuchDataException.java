package com.windry.chordplayer.exception;

import org.springframework.http.HttpStatus;

public class NoSuchDataException extends CustomRuntimeException{
    public NoSuchDataException() {
        super(HttpStatus.NOT_FOUND, ErrorCode.NO_SUCH_ELEMENT);
    }
}
