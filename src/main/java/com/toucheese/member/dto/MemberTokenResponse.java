package com.toucheese.member.dto;

import lombok.Builder;

@Builder
public record MemberTokenResponse(
        Long memberId,
        String email,
        String name,
        TokenDTO tokenDTO
) {

}
