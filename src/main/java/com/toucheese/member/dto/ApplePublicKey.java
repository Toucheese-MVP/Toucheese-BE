package com.toucheese.member.dto;

// Apple Public Key 조회
// JWT (id_token) 을 검증할 때 필요한 애플의 공개 키를 얻는 과정
public record ApplePublicKey(
        String kty,
        String kid,
        String alg,
        String n,
        String e
) {
}
