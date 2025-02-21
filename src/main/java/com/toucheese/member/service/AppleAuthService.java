package com.toucheese.member.service;


import com.toucheese.global.exception.ErrorCode;
import com.toucheese.global.exception.GlobalCustomException;
import com.toucheese.global.util.JwtTokenProvider;
import com.toucheese.member.dto.*;
import com.toucheese.member.client.AppleAuthClient;
import com.toucheese.member.util.ApplePublicKeyGenerator;
import com.toucheese.member.entity.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.naming.AuthenticationException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static io.jsonwebtoken.Jwts.SIG.ES256;


@Slf4j
@Service
@RequiredArgsConstructor
public class AppleAuthService {
    private final AppleAuthClient appleAuthClient;
    private final ApplePublicKeyGenerator applePublicKeyGenerator;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;
    private final TokenService tokenService;

    @Value("${apple.auth.client-id}")
    private String clientId;

    @Value("${apple.auth.key-id}")
    private String keyId;

    @Value("${apple.auth.team-id}")
    private String teamId;

    @Value("${apple.auth.private-key-path}")
    private String privateKeyPath;

    @Value("${apple.auth.audience}")
    private String audience;

    @Value("${apple.auth.redirect-uri}")
    private String redirectUrl;


    /**
     * Apple 로그인 요청을 처리하는 메서드
     */
    public SocalLoginCombinedResponse handleAppleLogin(AppleLoginRequest appleAuthRequest) throws AuthenticationException, NoSuchAlgorithmException, InvalidKeySpecException {
        AppleMember appleMember = getAppleMemberInfo(appleAuthRequest.idToken());
        Member member = memberService.findOrCreateMember(appleMember);
        String deviceId = appleAuthRequest.deviceId();
        TokenDTO tokenDTO = tokenService.loginMemberToken(member, deviceId);

        return new SocalLoginCombinedResponse(SocialLoginResponse.from(member, tokenDTO), tokenDTO.accessToken());
    }

    /**
     * Apple 의 공개키를 가져오는 메서드
     */
    public ApplePublicKeyResponse getAppleAuthPublicKey() {
        return appleAuthClient.getAppleAuthPublicKey();
    }

    /**
     * Apple ID Token 을 기반으로 회원 정보를 추출하는 메서드
     */
    public AppleMember getAppleMemberInfo(String identityToken) throws AuthenticationException, NoSuchAlgorithmException, InvalidKeySpecException {
        Map<String, String> headers = jwtTokenProvider.parseHeaders(identityToken);
        PublicKey publicKey = applePublicKeyGenerator.generatePublicKey(headers, getAppleAuthPublicKey());
        Claims claims = jwtTokenProvider.getTokenClaims(identityToken, publicKey);

        return new AppleMember(
                claims.getSubject(), // Apple userID
                (String) claims.get("name.firstName") + claims.get("name.lastName"),
                (String) claims.get("email")
        );
    }

    /**
     * Apple 서버 토큰 요청
     */
    private AppleAuthTokenResponse requestAppleToken(String code, boolean forLogin) throws IOException {
        AppleTokenRequest.AppleTokenRequestBuilder requestBuilder = AppleTokenRequest.builder()
                .code(code)
                .clientId(clientId)
                .clientSecret(createClientSecret())
                .grantType("authorization_code");
        if (forLogin) {
            requestBuilder.redirectUri(redirectUrl);
        }
        return appleAuthClient.getAppleToken(requestBuilder.build());
    }

    /**
     * Apple 로그인 콜백용 토큰 요청하는 메서드
     */
    public AppleLoginRequest getAppleTokenForCallback(String code) throws IOException {
        AppleAuthTokenResponse response = requestAppleToken(code, true);
        return AppleLoginRequest.builder()
                .platform("APPLE")
                .idToken(response.idToken())
                .build();
    }


    /**
     * Apple 회원 탈퇴용 토큰을 요청하는 메서드
     */
    public AppleAuthTokenResponse getAppleTokenForRevoke(String code) throws IOException {
        return requestAppleToken(code, false);
    }


    /**
     * Apple 계정 연동 해제 (회원 탈퇴) 요청을 수행하는 메서드
     */
    public boolean revoke(String authorizationCode) throws IOException {
        AppleAuthTokenResponse appleAuthToken = getAppleTokenForRevoke(authorizationCode);

        if (StringUtils.hasText(appleAuthToken.accessToken())) {
            AppleRevokeRequest revokeRequest = AppleRevokeRequest.builder()
                    .clientId(clientId)
                    .clientSecret(createClientSecret())
                    .token(appleAuthToken.accessToken())
                    .build();

            appleAuthClient.revokeAppleAuthToken(revokeRequest);
            return true;
        } else {
            throw new GlobalCustomException(ErrorCode.INVALID_ACCESS_TOKEN);
        }
    }

    /**
     * Apple Client Secret 생성
     */
    public String createClientSecret() throws IOException {
        Date expirationDate = Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());
        Map<String, Object> jwtHeader = new HashMap<>();
        jwtHeader.put("kid", keyId);
        jwtHeader.put("alg", "ES256");

        return Jwts.builder()
                .header().add(jwtHeader).and()
                .claim("iss", teamId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(expirationDate)
                .claim("aud", audience)
                .claim("sub", clientId)
                .signWith(getPrivateKey(), ES256)
                .compact();
    }

    /**
     * PrivateKey 로드
     */
    public PrivateKey getPrivateKey() {
        File file = new File(privateKeyPath);
        if (!file.exists() || !file.canRead()) {
            log.error("Private key 파일을 읽을 수 없습니다. 경로: {}", privateKeyPath);
            throw new GlobalCustomException(ErrorCode.FAIL_TO_LOAD_PRIVATE_KEY);
        }

        byte[] keyBytes = readPrivateKeyFile(file);
        return generatePrivateKey(keyBytes);
    }

    private byte[] readPrivateKeyFile(File file) {
        try (PemReader pemReader = new PemReader(new FileReader(file, StandardCharsets.UTF_8))) {
            PemObject pemObject = pemReader.readPemObject();
            if (pemObject == null) {
                throw new IOException("PEM 파일이 비어 있거나 올바르지 않습니다.");
            }
            return pemObject.getContent();
        } catch (IOException e) {
            log.error("Apple Private Key 파일을 읽는 중 오류 발생: {}", privateKeyPath, e);
            throw new GlobalCustomException(ErrorCode.FAIL_TO_LOAD_PRIVATE_KEY);
        }
    }

    private PrivateKey generatePrivateKey(byte[] keyBytes) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            log.error("Apple Private Key 파일을 파싱하는데 문제가 발생했습니다.", e);
            throw new GlobalCustomException(ErrorCode.FAIL_TO_LOAD_PRIVATE_KEY);
        }
    }
}
