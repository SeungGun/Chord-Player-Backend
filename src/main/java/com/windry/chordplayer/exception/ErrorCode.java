package com.windry.chordplayer.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    DUPLICATE_TITLE_ARTIST("4001", "이미 존재하는 제목과 가수입니다."),
    INVALID_PARAMETER("4002", "요구되는 입력 값이 유효하지 않거나 존재하지 않습니다."),
    INVALID_CHORD_MATCH("4003", "존재하지 않는 코드 형태입니다.");

    private final String code;
    private final String message;
}
