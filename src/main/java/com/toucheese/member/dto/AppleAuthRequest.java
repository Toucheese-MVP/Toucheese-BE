package com.toucheese.member.dto;

public record AppleAuthRequest(
        String idToken,
        String platform,
        String deviceId
) {
}
