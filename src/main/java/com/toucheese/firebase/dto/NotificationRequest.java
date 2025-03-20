package com.toucheese.firebase.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Map;

@Builder
public record NotificationRequest(
        @Schema(description = "알림 제목")
        @NotNull(message = "알림 제목은 필수입니다.")
        String title,
        @Schema(description = "알림 본문")
        String body,
        @Schema(description = "데이터 메세지 (앱이 포그라운드에서 처리하는 용도)")
        Map<String, String> data
) {
}
