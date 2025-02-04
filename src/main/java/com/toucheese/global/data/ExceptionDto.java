package com.toucheese.global.data;

import com.toucheese.global.exception.ErrorCode;
import jakarta.validation.constraints.NotNull;

public record ExceptionDto(
        @NotNull
        int code,
        @NotNull
        String message
) {
    public static ExceptionDto of(ErrorCode errorCode) {
        return new ExceptionDto(
                errorCode.getCode(),
                errorCode.getMessage()
        );
    }
}
