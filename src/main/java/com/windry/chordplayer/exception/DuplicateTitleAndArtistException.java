package com.windry.chordplayer.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DuplicateTitleAndArtistException extends CustomRuntimeException{

    public DuplicateTitleAndArtistException() {
        super(HttpStatus.BAD_REQUEST, ErrorCode.DUPLICATE_TITLE_ARTIST);
    }
}
