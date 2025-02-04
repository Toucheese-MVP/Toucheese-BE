package com.toucheese.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ToucheeseJwtException extends RuntimeException {

    private final ErrorCode errorCode;

    public ToucheeseJwtException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
