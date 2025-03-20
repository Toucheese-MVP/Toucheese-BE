package com.toucheese.firebase.controller;

import com.toucheese.firebase.dto.FcmDto;
import com.toucheese.firebase.dto.NotificationRequest;
import com.toucheese.global.data.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;


public interface FirebaseApi {
    @Operation(
            summary = "FCM 토큰 등록 및 업데이트",
            description = "사용자의 FCM 토큰을 저장하거나 업데이트합니다."
    )
    @PostMapping
    ResponseEntity<CommonResponse<?>> registerFcmToken(
            Principal principal,
            @RequestBody FcmDto fcmDto
    );

    @Operation(
            summary = "FCM 메세지 전송 (플랫폼 공통)",
            description = "FCM 메세지를 사용자에게 전송합니다."
    )
    @PostMapping
    ResponseEntity<CommonResponse<?>> sendNotification(
            Principal principal,
            @RequestBody NotificationRequest notificationRequest
    );
}
