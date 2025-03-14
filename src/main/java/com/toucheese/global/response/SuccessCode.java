package com.toucheese.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode implements ResponseCode {

    // 찜 목록
    ADD_FAVORITES_SUCCESS(HttpStatus.OK, "즐겨찾기 목록에 해당 스튜디오 추가 성공"),
    GET_FAVORITES_STUDIOS_SUCCESS(HttpStatus.OK, "해당 회원이 즐겨찾기한 스튜디오 목록 조회"),
    REMOVE_FAVORITES_SUCCESS(HttpStatus.OK, "즐겨찾기 목록에 해당 스튜디오 제거 성공");

    private final HttpStatus httpStatus; // HTTP 상태 필드
    private final String message; // 메세지 필드

    public int getHttpStatusCode(){
        return httpStatus.value();
    }
}
