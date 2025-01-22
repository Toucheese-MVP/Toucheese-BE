package com.toucheese.member.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.toucheese.global.util.JwtTokenProvider;
import com.toucheese.member.dto.AppleAuthRequest;
import com.toucheese.member.dto.ApplePublicKeyResponse;
import com.toucheese.member.dto.AppleMember;
import com.toucheese.member.client.AppleAuthClient;
import com.toucheese.member.util.ApplePublicKeyGenerator;
import com.toucheese.member.dto.SocalLoginCombinedResponse;
import com.toucheese.member.dto.SocialLoginResponse;
import com.toucheese.member.dto.TokenDTO;
import com.toucheese.member.entity.Member;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.naming.AuthenticationException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AppleAuthService {
    private final AppleAuthClient appleAuthClient;
    private final ApplePublicKeyGenerator applePublicKeyGenerator;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;
    private final TokenService tokenService;

    public ApplePublicKeyResponse getAppleAuthPublicKey() {
        return appleAuthClient.getAppleAuthPublicKey();
    }

    public SocalLoginCombinedResponse handleAppleLogin(AppleAuthRequest appleAuthRequest) throws
            AuthenticationException, NoSuchAlgorithmException, InvalidKeySpecException, JsonProcessingException
    {
        AppleMember appleMember = getAppleMemberInfo(appleAuthRequest.idToken()).block();

        assert appleMember != null;
        Member member = memberService.findOrCreateMember(appleMember);

        String deviceId = appleAuthRequest.deviceId();
        TokenDTO tokenDTO = tokenService.loginMemberToken(member, deviceId);

        return new SocalLoginCombinedResponse(SocialLoginResponse.from(member, tokenDTO), tokenDTO.accessToken());

    }

    public Mono<AppleMember> getAppleMemberInfo(String identityToken) throws
            JsonProcessingException, AuthenticationException, NoSuchAlgorithmException, InvalidKeySpecException
    {
        Map<String, String> headers = jwtTokenProvider.parseHeaders(identityToken);
        PublicKey publicKey = applePublicKeyGenerator.generatePublicKey(headers, getAppleAuthPublicKey());
        Claims claims = jwtTokenProvider.getTokenClaims(identityToken, publicKey);
        return Mono.just(new AppleMember(
                claims.getSubject(),
                (String) claims.get("name.firstName") + claims.get("name.lastName"),
                (String) claims.get("email")
        ));

    }
}
