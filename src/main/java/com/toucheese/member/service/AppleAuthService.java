package com.toucheese.member.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.toucheese.global.exception.ErrorCode;
import com.toucheese.global.exception.ToucheeseJwtException;
import com.toucheese.global.util.JwtTokenProvider;
import com.toucheese.member.dto.*;
import com.toucheese.member.client.AppleAuthClient;
import com.toucheese.member.entity.Token;
import com.toucheese.member.util.ApplePublicKeyGenerator;
import com.toucheese.member.entity.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import javax.naming.AuthenticationException;
import java.io.FileInputStream;
import java.io.IOException;
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

    @Value("${apple.revoke-url}")
    private String revokeUrl;

    @Value("${apple.token-url}")
    private String tokenUrl;


    public ApplePublicKeyResponse getAppleAuthPublicKey() {
        return appleAuthClient.getAppleAuthPublicKey();
    }

    public SocalLoginCombinedResponse handleAppleLogin(AppleAuthRequest appleAuthRequest) throws
            AuthenticationException, NoSuchAlgorithmException, InvalidKeySpecException, JsonProcessingException {
        AppleMember appleMember = getAppleMemberInfo(appleAuthRequest.idToken()).block();

        Member member = memberService.findOrCreateMember(appleMember);

        String deviceId = appleAuthRequest.deviceId();
        TokenDTO tokenDTO = tokenService.loginMemberToken(member, deviceId);

        return new SocalLoginCombinedResponse(SocialLoginResponse.from(member, tokenDTO), tokenDTO.accessToken());
    }

    public Mono<AppleMember> getAppleMemberInfo(String identityToken) throws
            AuthenticationException, NoSuchAlgorithmException, InvalidKeySpecException {
        Map<String, String> headers = jwtTokenProvider.parseHeaders(identityToken);
        PublicKey publicKey = applePublicKeyGenerator.generatePublicKey(headers, getAppleAuthPublicKey());
        Claims claims = jwtTokenProvider.getTokenClaims(identityToken, publicKey);

        return Mono.just(new AppleMember(
                claims.getSubject(), // Apple userID
                (String) claims.get("name.firstName") + claims.get("name.lastName"),
                (String) claims.get("email")
        ));
    }

    public AppleAuthTokenResponse generateAuthToken(String authorizationCode) throws IOException {
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", authorizationCode);
        params.add("client_id", clientId);
        params.add("client_secret", createClientSecret());
        params.add("grant_type", "authorization_code");

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // HttpEntity 설정
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

        // RestTemplate을 사용하여 애플 서버로 요청
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<AppleAuthTokenResponse> response = restTemplate.postForEntity(
                    tokenUrl,
                    requestEntity,
                    AppleAuthTokenResponse.class
            );
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                throw new ToucheeseJwtException(ErrorCode.APPLE_AUTH_TOKEN_FAIL);
            }
        } catch (HttpClientErrorException e) {
            log.error(String.valueOf(e));
            throw new ToucheeseJwtException(ErrorCode.APPLE_AUTH_TOKEN_FAIL);
        }

    }

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

    private PrivateKey getPrivateKey() throws IOException{
        try (FileInputStream fis = new FileInputStream(privateKeyPath)) {
            byte[] keyBytes = fis.readAllBytes();
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            return keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new ToucheeseJwtException(ErrorCode.FAIL_TO_LOAD_PRIVATE_KEY);
        }

    }

    public void revokeAppleAccessToken(String authorizationCode) throws IOException {
        try {
            AppleAuthTokenResponse appleAuthToken = generateAuthToken(authorizationCode);
            if (!StringUtils.hasText(appleAuthToken.accessToken())) {
                RestTemplate restTemplate = new RestTemplateBuilder().build();
                LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
                params.add("client_id", clientId);
                params.add("client_secret", createClientSecret());
                params.add("token", appleAuthToken.accessToken());
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);
                restTemplate.postForEntity(revokeUrl, httpEntity, String.class);
            } else {
                throw new ToucheeseJwtException(ErrorCode.INVALID_APPLE_ACCESS_TOKEN);
            }
        } catch (IOException e) {
            throw new ToucheeseJwtException(ErrorCode.APPLE_REVOKE_TOKEN_FAIL);
        }

    }

}
