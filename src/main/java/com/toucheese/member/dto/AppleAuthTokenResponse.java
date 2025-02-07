package com.toucheese.member.dto;

public record AppleAuthTokenResponse(
        String accessToken,
        Integer expiredIn,
        String idToken,
        String refreshToken,
        String tokenType
) {
}
