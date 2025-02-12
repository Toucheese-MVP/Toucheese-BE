package com.toucheese.member.dto;

import lombok.Builder;

@Builder
public record AppleTokenRequest(
        String code,
        String clientId,
        String clientSecret,
        String redirectUri,
        String grantType
) {

}
