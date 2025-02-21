package com.toucheese.global.util;

import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toucheese.global.exception.ErrorCode;
import com.toucheese.global.exception.GlobalCustomException;
import com.toucheese.member.entity.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import com.toucheese.global.config.AppConfig;
import com.toucheese.global.data.JwtValidateStatus;

import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final ObjectMapper objectMapper;

    @Value("${jwt.access-token-expiration}")
    private Long accessTokenExpiration;
    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;


    public JwtTokenProvider(AppConfig appConfig, ObjectMapper objectMapper) {
        this.secretKey = Keys.hmacShaKeyFor(appConfig.getSecretKey());
        this.objectMapper = objectMapper;
    }

    /**
     * 접근 토큰 생성을 위한 메서드
     * @param memberId subject 등록을 위한 회원 아이디
     * @return 생성된 접근 토큰
     */
    public String createAccessToken(String memberId, Role role) {
        Date now = new Date();
        return Jwts.builder()
                .subject(memberId)
                .claim("role", role.toString())
                .signWith(secretKey)
                .expiration(new Date(now.getTime() + accessTokenExpiration)) // 30분
                .issuedAt(now)
                .compact();
    }

    /**
     * 갱신 토큰 생성을 위한 메서드
     * @param memberId subject 등록을 위한 회원 아이디
     * @return 생성된 갱신 토큰
     */
    public String createRefreshToken(String memberId) {
        Date now = new Date();
        return Jwts.builder()
                .subject(memberId)
                .signWith(secretKey)
                .expiration(new Date(now.getTime() + refreshTokenExpiration))
                .issuedAt(now)
                .compact();
    }

    /**
     * 토큰 내 정보를 확인하기 위한 메서드
     * @param token 접근 토큰 혹은 갱신 토큰
     * @return 토큰 내 정보
     */
    public Claims getClaims(String token) {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
    }

    /**
     * 토큰 검증을 위한 메서드
     * @param token 토큰
     * @param isRefreshToken false : 액세스 토큰, true : 리프레시 토큰
     * @return 토큰이 유효하면 true, 아니면 false
     */
    public boolean validateToken(String token, boolean isRefreshToken) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰 입니다: {}", e.getMessage());
            throw new GlobalCustomException(
                    isRefreshToken ? ErrorCode.EXPIRED_REFRESH_TOKEN : ErrorCode.EXPIRED_ACCESS_TOKEN
            );

        } catch (MalformedJwtException e) {
            log.error("잘못된 형식의 JWT 토큰입니다: {}", e.getMessage());
            throw new GlobalCustomException(
                    isRefreshToken ? ErrorCode.MALFORMED_REFRESH_TOKEN : ErrorCode.MALFORMED_ACCESS_TOKEN
            );
        } catch (SignatureException e) {
            log.error("JWT 서명이 유효하지 않습니다: {}", e.getMessage());
            throw new GlobalCustomException(
                    isRefreshToken ? ErrorCode.INVALID_REFRESH_TOKEN : ErrorCode.INVALID_ACCESS_TOKEN
            );
        }
    }



    /**
     * 인증 정보 설정을 위한 메서드
     * @param token 접근 토큰
     * @return 인증 정보
     */
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        String role = claims.get("role", String.class);
        User principal = new User(claims.getSubject(), "",  Collections.singleton(() -> role));
        return new UsernamePasswordAuthenticationToken(principal, token, principal.getAuthorities());
    }


    public Map<String, String> parseHeaders(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new GlobalCustomException(ErrorCode.INVALID_ID_TOKEN);
        }
        String header = decodeHeader(parts[0]);
        try {
            return objectMapper.readValue(header, new TypeReference<Map<String, String>>() {
            });
        } catch (JsonProcessingException e) {
            throw new GlobalCustomException(ErrorCode.INVALID_HEADER_PARSING);
        }
    }

    public String decodeHeader(String token) {
        return new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
    }

    public Claims getTokenClaims(String token, PublicKey publicKey) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
