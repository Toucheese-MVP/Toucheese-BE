package com.toucheese.firebase.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record FcmMessageRequest(
        @Schema(description = "메세지 제목")
        String title,
        @Schema(description = "메세지 내용")
        String body
) {

}
