package com.toucheese.firebase.controller;


import com.toucheese.firebase.dto.FcmMessageRequest;
import com.toucheese.firebase.dto.FcmTokenRequest;
import com.toucheese.firebase.service.FirebaseMessageService;
import com.toucheese.global.util.PrincipalUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/v1/fcm")
@Tag(name = "FCM 알림 기능")
@RequiredArgsConstructor
public class FirebaseController {
    private final FirebaseMessageService firebaseMessageService;

    /**
     * FCM 토큰 등록 및 업데이트
     *
     * @param principal 사용자 정보
     * @param fcmTokenRequest fcm 토큰
     * @return 저장 or 업데이트
     */
    @PostMapping
    public ResponseEntity<Void> registerFcmToken(
            Principal principal,
            @RequestBody FcmTokenRequest fcmTokenRequest
    ) {
        Long memberId = PrincipalUtils.extractMemberId(principal);
        firebaseMessageService.saveOrUpdateToken(memberId, fcmTokenRequest.fcmToken());
        return ResponseEntity.ok().build();

    }


    @PostMapping("/sendMessage")
    public ResponseEntity<String> sendMessage(
            @RequestBody FcmMessageRequest requestDto
    ) {
        String response = String.valueOf(firebaseMessageService.sendMessage(requestDto));
        return ResponseEntity.ok(response);
    }
}
