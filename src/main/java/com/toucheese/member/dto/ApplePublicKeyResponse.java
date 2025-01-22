package com.toucheese.member.dto;

import javax.naming.AuthenticationException;
import java.util.List;

// Apple Public Key를 조회하면 2개의 Key가 조회되고, 그 중 kid와 alg가 일치하는 것을 사용하면 됨
public record ApplePublicKeyResponse(List<ApplePublicKey> keys) {
    public ApplePublicKey getMatchedKey(String kid, String alg) throws AuthenticationException {
        return keys.stream()
                .filter(key -> key.kid().equals(kid) && key.alg().equals(alg))
                .findAny()
                .orElseThrow(AuthenticationException::new);
    }

}
