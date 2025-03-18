package com.toucheese.firebase.dto;

import lombok.Builder;

@Builder
public record FcmTokenRequest(
    String fcmToken
) {
}
