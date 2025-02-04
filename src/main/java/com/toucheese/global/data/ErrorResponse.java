package com.toucheese.global.data;

import com.toucheese.global.exception.ErrorCode;

public record ErrorResponse<T> (
        boolean success,
        T payload,
        Object error
) {
    public static <T> ErrorResponse<T> fail(ErrorCode errorCode) {
        return new ErrorResponse<>(false, null, ExceptionDto.of(errorCode));
    }
}
