package com.toucheese.member.util;

import com.toucheese.member.dto.ApplePublicKey;
import com.toucheese.member.dto.ApplePublicKeyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.naming.AuthenticationException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;

/**
 * Apple Public Key를 바탕으로 Public Key를 생성하는 Bean이다.
 * client로부터 받은 identity token을 디코딩 하여 header를 얻어와,
 * 조회한 Apple Public Key들 중 매치되는 Key를 찾아 생성
 */
@Component
@RequiredArgsConstructor
public class ApplePublicKeyGenerator {
    public PublicKey generatePublicKey(
            Map<String, String> tokenHeaders,
            ApplePublicKeyResponse applePublicKeys
    ) throws AuthenticationException, NoSuchAlgorithmException, InvalidKeySpecException {
        ApplePublicKey publicKey = applePublicKeys.getMatchedKey(tokenHeaders.get("kid"), tokenHeaders.get("alg"));
        return getPublicKey(publicKey);
    }

    private PublicKey getPublicKey(ApplePublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] nBytes = Base64.getUrlDecoder().decode(publicKey.n());
        byte[] eBytes = Base64.getUrlDecoder().decode(publicKey.e());

        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(
                new BigInteger(1, nBytes),
                new BigInteger(1, eBytes)
        );

        KeyFactory keyFactory = KeyFactory.getInstance(publicKey.kty());
        return keyFactory.generatePublic(publicKeySpec);
    }
}
