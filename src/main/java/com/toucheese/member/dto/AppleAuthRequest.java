package com.toucheese.member.dto;

public record AppleAuthRequest(
        String idToken,
        String authorizationCode,
        String platform,
        String deviceId
) {
}
