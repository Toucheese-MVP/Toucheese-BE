package com.toucheese.global.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.toucheese.global.exception.ErrorCode;
import jakarta.annotation.Nullable;
import org.springframework.http.HttpStatus;

public record CommonResponse<T> (
        @JsonIgnore
        HttpStatus httpStatus,
        boolean success,
        @Nullable
        T payload,
        @Nullable
        Object error
) {
    // 일반적인 성공
    public static <T> CommonResponse<T> ok(@Nullable final T data) {
        return new CommonResponse<>(HttpStatus.OK, true, data, null);
    }

    public static <T> CommonResponse<T> created(@Nullable final T data) {
        return new CommonResponse<>(HttpStatus.CREATED, true, data, null);
    }

    // 실패
    public static <T> CommonResponse<T> fail(ErrorCode errorCode) {
        return new CommonResponse<>(errorCode.getHttpStatus(), false, null,ExceptionDto.of(errorCode));
    }
}
