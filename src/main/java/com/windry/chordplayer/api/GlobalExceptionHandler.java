package com.windry.chordplayer.api;

import com.windry.chordplayer.dto.ExceptionResponse;
import com.windry.chordplayer.exception.CustomRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomRuntimeException.class)
    public ResponseEntity<ExceptionResponse> handleRuntimeCustomException(CustomRuntimeException e) {
        log.error(e.getMessage());

        return ResponseEntity
                .status(e.getStatus())
                .body(new ExceptionResponse(e.getErrorCode().getCode(), e.getMessage()));
    }

}
