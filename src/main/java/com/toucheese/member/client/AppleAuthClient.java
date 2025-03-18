package com.toucheese.member.client;

import com.toucheese.global.config.AppleOAuthConfig;
import com.toucheese.member.dto.AppleAuthTokenResponse;
import com.toucheese.member.dto.ApplePublicKeyResponse;
import com.toucheese.member.dto.AppleRevokeRequest;
import com.toucheese.member.dto.AppleTokenRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

@FeignClient(
        name = "appleAuthClient",
        url = "https://appleid.apple.com/auth",
        configuration = AppleOAuthConfig.class
)
public interface AppleAuthClient {
    @GetMapping(value = "/keys")
    ApplePublicKeyResponse getAppleAuthPublicKey();

    @PostMapping(value = "/token", consumes = APPLICATION_FORM_URLENCODED_VALUE)
    AppleAuthTokenResponse getAppleToken(@RequestBody AppleTokenRequest request);

    @PostMapping(value = "/revoke", consumes = "application/x-www-form-urlencoded")
    void revokeAppleAuthToken(AppleRevokeRequest request);

}
