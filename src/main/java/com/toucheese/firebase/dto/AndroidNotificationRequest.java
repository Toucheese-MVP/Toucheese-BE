package com.toucheese.firebase.dto;

import lombok.Builder;

import java.util.Map;

@Builder
public record AndroidNotificationRequest(
        String title,
        String body,
        Map<String, String> data
) {

}
