package com.toucheese.member.dto;

import com.toucheese.member.entity.Member;

import lombok.Builder;

@Builder
public record SocialLoginResponse(
        Long memberId,
        String email,
        String nickname,
        boolean isFirstLogin,
        String refreshToken, // 시스템에서 발급한 리프레시 토큰
        String deviceId // 기기 ID
) {
    public static SocialLoginResponse from(Member member, TokenDTO tokenDTO) {
        return SocialLoginResponse.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .nickname(member.getName())
                .isFirstLogin(member.isFirstLogin())
                .refreshToken(tokenDTO.refreshToken())
                .deviceId(tokenDTO.deviceId())
                .build();
    }
}