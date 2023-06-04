package com.windry.chordplayer.dto;

import com.windry.chordplayer.exception.ErrorCode;
import lombok.Data;

@Data
public class ExceptionResponse {
    private String errorCode;
    private String message;

    public ExceptionResponse(ErrorCode errorCode) {
        this.errorCode = errorCode.getCode();
    }

    public ExceptionResponse(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
