package com.windry.chordplayer.exception;

import org.springframework.http.HttpStatus;

public class ImpossibleConvertGenderException extends CustomRuntimeException {
    public ImpossibleConvertGenderException() {
        super(HttpStatus.FORBIDDEN, ErrorCode.IMPOSSIBLE_MIXED_GENDER_CONVERT);
    }
}
