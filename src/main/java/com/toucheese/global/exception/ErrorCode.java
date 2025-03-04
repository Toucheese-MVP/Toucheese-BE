package com.toucheese.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    /* 토큰 관련 */
    EXPIRED_REFRESH_TOKEN(4001, HttpStatus.UNAUTHORIZED, "만료된 리프레시 토큰입니다, 재로그인이 필요합니다."),
    INVALID_REFRESH_TOKEN(4002, HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    MALFORMED_REFRESH_TOKEN(4020, HttpStatus.UNAUTHORIZED, "잘못된 형식의 리프레시 토큰입니다."),
    REFRESH_TOKEN_MISMATCH(4003, HttpStatus.BAD_REQUEST, "다바이스 ID에 해당하는 리프레시 토큰이 서로 일치하지 않습니다"),
    TOKEN_NOT_FOUND(4004, HttpStatus.BAD_REQUEST, "디바이스 ID에 해당하는 리프레시 토큰을 찾을 수 없습니다."),
    EXPIRED_ACCESS_TOKEN(4005, HttpStatus.UNAUTHORIZED, "만료된 액세스 토큰입니다."),
    INVALID_ACCESS_TOKEN(4006, HttpStatus.UNAUTHORIZED, "유효하지 않은 액세스 토큰입니다."),
    MALFORMED_ACCESS_TOKEN(4007, HttpStatus.UNAUTHORIZED, "잘못된 형식의 액세스 토큰입니다."),

    /* 예약 관련 */
    RESERVATION_NOT_FOUND(4010, HttpStatus.NOT_FOUND, "예약된 정보가 없습니다."),
    RESERVATION_STATUS_NOT_FOUND(4020, HttpStatus.NOT_FOUND, "예약된 정보의 상태를 찾을 수 없습니다."),

    /* 회원 관련 */
    PHONE_REQUEST_NOT_FOUND(4011, HttpStatus.NOT_FOUND, "요청에 전화번호 필드가 누락되었습니다."),
    MEMBER_NOT_FOUND(4012, HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),

    /* 소셜 로그인 관련 */
    INVALID_ID_TOKEN(4008, HttpStatus.BAD_REQUEST, "[Apple Login] ID 토큰이 유효하지 않습니다."),
    INVALID_HEADER_PARSING(4009, HttpStatus.BAD_REQUEST, "[Apple Login] IdToken 헤더 파싱에 실패했습니다."),
    APPLE_AUTH_TOKEN_FAIL(4013, HttpStatus.INTERNAL_SERVER_ERROR, "애플 서버로의 인증 토큰 요청에 실패하였습니다."),
    FAIL_TO_LOAD_PRIVATE_KEY(4014, HttpStatus.INTERNAL_SERVER_ERROR, "애플 private 키를 가져오는데 실패하였습니다."),
    INVALID_APPLE_ACCESS_TOKEN(4015, HttpStatus.BAD_REQUEST, "애플 Access Token 이 유효하지 않습니다."),
    APPLE_REVOKE_TOKEN_FAIL(4016, HttpStatus.BAD_REQUEST, "애플 Access Token 무효화에 실패하였습니다."),
    LOGOUT_UNAUTHORIZED_ACCESS(4017, HttpStatus.UNAUTHORIZED, "로그아웃에 실패하였습니다."),
    KAKAO_WITHDRAW_FAIL(4018, HttpStatus.INTERNAL_SERVER_ERROR, "카카오 회원 탈퇴에 실패했습니다."),

    
    // NOT_SAME_PASSWORD(4020, HttpStatus.BAD_REQUEST, "아이디 및 비밀번호가 일치하지 않습니다.")

    AUTHORIZATION_CODE_EXPIRED(4019, HttpStatus.BAD_REQUEST, "Authorization Code가 만료되었습니다."),


    ;

    private final int code;
    private final HttpStatus httpStatus;
    private final String message;
}
