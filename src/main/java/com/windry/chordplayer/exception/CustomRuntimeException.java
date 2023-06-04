package com.windry.chordplayer.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomRuntimeException extends RuntimeException{
    private final HttpStatus status;
    private final ErrorCode errorCode;

    public CustomRuntimeException(HttpStatus status, ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.status = status;
        this.errorCode = errorCode;
    }
}
