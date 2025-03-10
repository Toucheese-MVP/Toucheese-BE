package com.toucheese.global.exception;


import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        String responseBody = getResponseBody(response);

        log.error("FeignClient Error: methodKey = {}, status = {}, body = {}", methodKey, response.status(), responseBody);


        return switch (response.status()) {
            case 408 -> {
                log.error("Request Timeout: methodKey = {}, status = {}, body = {}", methodKey, response.status(), responseBody);
                yield new GlobalCustomException(ErrorCode.REQUEST_TIMEOUT);
            }

            case 400 -> {
                if (methodKey.contains("getAppleToken")) {
                    // 애플 서버로부터 AccessToken 관련 오류 처리
                    yield new GlobalCustomException(ErrorCode.INVALID_APPLE_ACCESS_TOKEN);
                }
                if (methodKey.contains("revokeAppleAuthToken")) {
                    // 애플 서버에서 토큰 무효화 실패 시
                    yield new GlobalCustomException(ErrorCode.APPLE_REVOKE_TOKEN_FAIL);
                }
                if (methodKey.contains("getAppleTokenForRevoke")) {
                    // Authorization Code 만료 관련 오류 처리
                    yield new GlobalCustomException(ErrorCode.AUTHORIZATION_CODE_EXPIRED);
                }
                // 기본 400 에러 처리
                yield new GlobalCustomException(ErrorCode.APPLE_AUTH_TOKEN_FAIL);
            }
            case 401 -> {
                if (methodKey.contains("getAppleToken")) {
                    // 유효하지 않은 AccessToken 또는 인증 정보 처리
                    yield new GlobalCustomException(ErrorCode.INVALID_APPLE_ACCESS_TOKEN);
                }
                // 기본 401 에러 처리
                yield new ToucheeseUnAuthorizedException("Unauthorized: Invalid credentials");
            }

            case 500 -> {
                if (methodKey.contains("getAppleToken")) {
                    yield new GlobalCustomException(ErrorCode.APPLE_AUTH_TOKEN_FAIL);
                }
                if (methodKey.contains("revokeAppleAuthToken")) {
                    yield new GlobalCustomException(ErrorCode.APPLE_REVOKE_TOKEN_FAIL);
                }
                yield new ToucheeseInternalServerErrorException("Internal server error");
            }

            default -> throw new IllegalStateException("Unexpected response status: " + response.status());
        };
    }

    private String getResponseBody(Response response) {
        try {
            if (response.body() != null) {
                return Util.toString(response.body().asReader(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            log.error("Failed to read Feign response body", e);
        }
        return "";
    }
}

