package com.toucheese.member.dto;

import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.NotBlank;

public record AppleAuthRequest(
        @NotBlank(message = "idToken은 필수 값입니다.")
        String idToken,
        @NotBlank
        String platform,
        @Nullable
        String deviceId
) {
}
