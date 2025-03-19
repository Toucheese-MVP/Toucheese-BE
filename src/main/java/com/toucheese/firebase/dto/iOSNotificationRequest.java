package com.toucheese.firebase.dto;

import lombok.Builder;

import java.util.Map;

@Builder
public record iOSNotificationRequest(
        String alert,
        Integer badge,
        String sound,
        Map<String, String> customData
) {
}
