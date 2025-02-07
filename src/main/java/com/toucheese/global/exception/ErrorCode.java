package com.toucheese.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    /* 4000 ~ 4999: 클라이언트 에러 */

    // 토큰 관련 에러 (4001 ~ 4099)
    EXPIRED_REFRESH_TOKEN(4001, HttpStatus.UNAUTHORIZED, "Refresh 토큰이 만료되었습니다, 재로그인이 필요합니다."),
    INVALID_REFRESH_TOKEN(4002, HttpStatus.FORBIDDEN, "Refresh 토큰이 유효하지 않습니다, 재로그인이 필요합니다."),
    REFRESH_TOKEN_MISMATCH(4003, HttpStatus.BAD_REQUEST, "Refresh 토큰이 일치하지 않습니다, 재로그인이 필요합니다."),
    TOKEN_NOT_FOUND(4004, HttpStatus.BAD_REQUEST, "deviceId에 해당하는 토큰을 찾을 수 없습니다."),
    EXPIRED_ACCESS_TOKEN(4005, HttpStatus.UNAUTHORIZED, "Access 토큰이 만료되었습니다."),
    INVALID_ACCESS_TOKEN(4006, HttpStatus.FORBIDDEN, "Access 토큰이 유효하지 않습니다."),
    INVALID_TOKEN_STATUS(4007, HttpStatus.INTERNAL_SERVER_ERROR, "올바르지 않은 토큰 상태입니다."),
    INVALID_ID_TOKEN(4008, HttpStatus.BAD_REQUEST, "[Apple Login] ID 토큰이 유효하지 않습니다."),
    INVALID_HEADER_PARSING(4009, HttpStatus.BAD_REQUEST, "[Apple Login] IdToken 헤더 파싱에 실패했습니다."),
    RESERVATION_NOT_FOUND(4010, HttpStatus.NOT_FOUND, "예약된 정보가 없습니다."),
    PHONE_NOT_FOUND(4011, HttpStatus.NOT_FOUND, "전화번호가 없는 예약입니다."),
    MEMBER_NOT_FOUND(4012, HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다.")      
    APPLE_AUTH_TOKEN_FAIL(4013, HttpStatus.INTERNAL_SERVER_ERROR, "애플 인증 토큰 생성에 실패하였습니다."),
    FAIL_TO_LOAD_PRIVATE_KEY(4014, HttpStatus.INTERNAL_SERVER_ERROR, "Apple private 키를 가져오는데 실패하였습니다."),
    INVALID_APPLE_ACCESS_TOKEN(4015, HttpStatus.BAD_REQUEST, "애플 액세스 토큰이 유효하지 않습니다."),
    APPLE_REVOKE_TOKEN_FAIL(4016, HttpStatus.BAD_REQUEST, "애플 액세스 토큰 무효화에 실패하였습니다."),
    LOGOUT_UNAUTHORIZED_ACCESS(4017, HttpStatus.UNAUTHORIZED, "로그아웃에 실패하였습니다.")      
    ;


    // 회원 관련 에러 (4101 ~ 4199)
    // 스튜디오 관련 에러 (4201 ~ 4299)
    // 기타 (4301 ~ 4399)

    private final int code;
    private final HttpStatus httpStatus;
    private final String message;
}
