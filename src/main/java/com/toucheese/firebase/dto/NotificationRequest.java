package com.toucheese.firebase.dto;

import com.google.firebase.messaging.Notification;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;


@Builder
public record NotificationRequest(
        @Schema(description = "알림 제목")
        @NotBlank
        String title,
        @Schema(description = "알림 본문")
        String body
) {
        @Builder
        public NotificationRequest {}

        public Notification toNotification() {
                return Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build();
        }

}
