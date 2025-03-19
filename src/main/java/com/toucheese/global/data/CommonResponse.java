package com.toucheese.global.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.toucheese.global.exception.ErrorCode;
import jakarta.annotation.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public record CommonResponse<T> (
        @JsonIgnore
        HttpStatus httpStatus,
        boolean success,
        @Nullable
        T payload,
        @Nullable
        Object error
) {
    // 성공(Default)
    public static <T> CommonResponse<T> ok(@Nullable final T data) {
        return new CommonResponse<>(HttpStatus.OK, true, data, null);
    }

    // 성공(Created)
    public static <T> CommonResponse<T> created(@Nullable final T data) {
        return new CommonResponse<>(HttpStatus.CREATED, true, data, null);
    }

    // 성공(Updated)
    public static <T> CommonResponse<T> updated(@Nullable final T data) {
        return new CommonResponse<>(HttpStatus.OK, true, data, null);
    }

    // 실패
    public static <T> CommonResponse<T> fail(ErrorCode errorCode) {
        return new CommonResponse<>(errorCode.getHttpStatus(), false, null,ExceptionDto.of(errorCode));
    }

    // ResponseEntity 로 변환
    public ResponseEntity<CommonResponse<?>> toResponseEntity() {
        return new ResponseEntity<>(this, httpStatus);
    }

}
