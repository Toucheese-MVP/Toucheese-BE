package com.toucheese.firebase.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record FcmMessageRequest(
        @Schema(description = "사용자 ID")
        Long memberId,
        @Schema(description = "메세지 제목")
        String title,
        @Schema(description = "메세지 내용")
        String body
) {

}
