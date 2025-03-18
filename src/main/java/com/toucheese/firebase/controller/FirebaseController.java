package com.toucheese.firebase.controller;


import com.toucheese.firebase.dto.FcmMessageRequest;
import com.toucheese.firebase.dto.FcmDto;
import com.toucheese.firebase.service.FirebaseMessageService;
import com.toucheese.global.util.PrincipalUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@Tag(name = "FCM 알림 기능", description = "Firebase Cloud Messaging 관련 API")
@RequiredArgsConstructor
public class FirebaseController {
    private final FirebaseMessageService firebaseMessageService;

    /**
     * FCM 토큰 등록 및 업데이트
     *
     * @param principal 사용자 인증 정보
     * @param fcmDto fcm 토큰
     * @return 저장 or 업데이트
     */
    @Operation(
            summary = "FCM 토큰 등록 및 업데이트",
            description = "사용자의 FCM 토큰을 저장하거나 업데이트합니다.",
            parameters = {
                @Parameter(name = "fcmDto", description = "FCM 토큰 정보", required = true)
            }
    )
    @PostMapping
    public ResponseEntity<Void> registerFcmToken(
            Principal principal,
            @RequestBody FcmDto fcmDto
    ) {
        Long memberId = PrincipalUtils.extractMemberId(principal);
        firebaseMessageService.saveOrUpdateToken(memberId, fcmDto.fcmToken());
        return ResponseEntity.ok().build();

    }

    /**
     * FCM 메세지 전송
     *
     * @param principal 사용자 인증 정보
     * @param fcmMessageRequest 메세지 내용 (title, body)
     * @return 매세지 전송 결과
     */
    @PostMapping("/sendMessage")
    @Operation(
            summary = "FCM 메세지 전송",
            description = "FCM 메세지를 사용자에게 전송합니다.",
            parameters = {
                    @Parameter(name = "fcmRequestDto", description = "FCM 토큰 정보", required = true)
            }
    )
    public ResponseEntity<String> sendMessage(
            Principal principal,
            @RequestBody FcmMessageRequest fcmMessageRequest
    ) {
        Long memberId = PrincipalUtils.extractMemberId(principal);
        String response = firebaseMessageService.sendMessage(memberId, fcmMessageRequest);
        return ResponseEntity.ok(response);
    }
}
