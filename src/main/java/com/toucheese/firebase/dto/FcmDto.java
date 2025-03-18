package com.toucheese.firebase.dto;

import lombok.Builder;

@Builder
public record FcmDto(
    String fcmToken
) {
}
