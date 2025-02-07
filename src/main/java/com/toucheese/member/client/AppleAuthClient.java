package com.toucheese.member.client;

import com.toucheese.member.dto.ApplePublicKeyResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "appleAuthClient", url = "${apple.public-key-url}")
public interface AppleAuthClient {
    @GetMapping
    ApplePublicKeyResponse getAppleAuthPublicKey();

}
