package com.toucheese.member.dto;

import lombok.Builder;

@Builder
public record AppleRevokeRequest(
        String clientId,
        String clientSecret,
        String token // accessToken 과 refreshToken
) {
}
