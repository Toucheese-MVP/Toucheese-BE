package com.toucheese.member.dto;

import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record AppleLoginRequest(
        @NotBlank(message = "idToken은 필수 값입니다.")
        String idToken,
        @NotBlank
        String platform,
        @Nullable
        String deviceId
) {
}
