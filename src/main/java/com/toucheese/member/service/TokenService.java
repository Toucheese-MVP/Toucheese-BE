package com.toucheese.member.service;

import java.util.UUID;

import com.toucheese.global.exception.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.toucheese.global.util.JwtTokenProvider;
import com.toucheese.member.dto.MemberTokenResponse;
import com.toucheese.member.dto.ReissueRequest;
import com.toucheese.member.dto.TokenDTO;
import com.toucheese.member.entity.Member;
import com.toucheese.member.entity.Token;
import com.toucheese.member.repository.TokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRepository tokenRepository;

    /**
     * 토큰 재발급을 위한 메서드
     *
     * @param reissueRequest 재발급을 위한 검증 정보
     * @return 재발급 된 AccessToken 및 로그인 정보
     */
    @Transactional
    public MemberTokenResponse reissueAccessToken(ReissueRequest reissueRequest) {
        String deviceId = reissueRequest.deviceId();
        Token token = findTokenByDeviceId(deviceId);
        String refreshToken = token.getRefreshToken();

        // 내가 요청 바디에 담은 리프레시 토큰과 deviceId 로 찾은 리프레시 토큰이 다를떄
        checkRefreshToken(refreshToken, reissueRequest.refreshToken());
        String memberId = jwtTokenProvider.getClaims(refreshToken).getSubject();
        Member member = token.getMember();
        String newAccessToken = jwtTokenProvider.createAccessToken(memberId, member.getRole());

        return MemberTokenResponse.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .tokenDTO(
                        TokenDTO.builder()
                                .refreshToken(refreshToken)
                                .accessToken(newAccessToken)
                                .deviceId(deviceId)
                                .build()
                )
                .build();
    }

    /**
     * 회원 정보와 기기 정보를 통해 해당되는 토큰을 검색
     *
     * @param deviceId 기기 아이디
     * @return 토큰 정보
     */
    @Transactional(readOnly = true)
    public Token findTokenByDeviceId(String deviceId) {
        return tokenRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.TOKEN_NOT_FOUND));
    }

    /**
     * 로그인 시 회원 토큰 처리 메서드
     *
     * @param member   회원 정보
     * @param deviceId 기기 아이디
     * @return 기록된 토큰 정보
     */
    @Transactional
    public TokenDTO loginMemberToken(Member member, String deviceId) {
        TokenDTO tokenDTO = generateTokens(member);

        if (validDeviceId(deviceId)) {
            deviceId = UUID.randomUUID().toString();
            saveToken(member, deviceId, tokenDTO);
        } else {
            Token token = findTokenByDeviceId(deviceId);
            token.updateRefreshToken(tokenDTO.refreshToken());
        }

        return TokenDTO.of(tokenDTO.accessToken(), tokenDTO.refreshToken(), deviceId);
    }

    /**
     * 기기 아이디 검증을 위한 메서드
     *
     * @param deviceId 기기 아이디
     * @return true / false
     */
    public boolean validDeviceId(String deviceId) {
        return deviceId == null || deviceId.isBlank();
    }

    /**
     * 새 로그인 시 토큰 저장을 위한 메서드
     *
     * @param member   회원 정보
     * @param deviceId 기기 아이디
     * @param tokenDTO 생성된 토큰 정보
     */
    @Transactional
    public void saveToken(Member member, String deviceId, TokenDTO tokenDTO) {
        Token token = Token.builder()
                .member(member)
                .refreshToken(tokenDTO.refreshToken())
                .deviceId(deviceId)
                .build();

        tokenRepository.save(token);
    }

    /**
     * AccessToken, RefreshToken 생성 메서드
     *
     * @param member 회원 정보
     * @return 생성된 토큰
     */
    public TokenDTO generateTokens(Member member) {
        String memberId = member.getId().toString();
        String refreshToken = jwtTokenProvider.createRefreshToken(memberId);
        String accessToken = jwtTokenProvider.createAccessToken(memberId, member.getRole());

        return TokenDTO.builder()
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .build();
    }

    /**
     * RefreshToken 검증
     *
     * @param refreshToken 저장된 리프레시 토큰
     * @param requestRefreshToken 요청한 리프레시 토큰
     */
    public void checkRefreshToken(String refreshToken, String requestRefreshToken) {
        jwtTokenProvider.validateToken(refreshToken, true);
        if (!refreshToken.equals(requestRefreshToken)) {
            throw new GlobalCustomException(ErrorCode.REFRESH_TOKEN_MISMATCH);
        }
    }

    /**
     * 로그아웃 처리 메서드
     *
     * @param memberId 회원 ID
     * @param deviceId 다바이스 ID
     */
    public void logout(Long memberId, String deviceId) {
        Token token = tokenRepository.findByMemberIdAndDeviceId(memberId, deviceId)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.TOKEN_NOT_FOUND));
        if (!token.getMember().getId().equals(memberId)) {
            throw new GlobalCustomException(ErrorCode.LOGOUT_UNAUTHORIZED_ACCESS);
        }
        token.updateRefreshToken(null);
        tokenRepository.save(token);
    }
}
