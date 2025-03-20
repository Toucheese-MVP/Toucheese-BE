package com.toucheese.firebase.controller;


import com.toucheese.firebase.dto.FcmDto;
import com.toucheese.firebase.dto.NotificationRequest;
import com.toucheese.firebase.service.FirebaseMessageService;
import com.toucheese.global.data.CommonResponse;
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
@Tag(name = "FCM 앱 푸시알람", description = "Firebase Cloud Messaging 관련 API")
@RequiredArgsConstructor
public class FirebaseController implements FirebaseApi {
    private final FirebaseMessageService firebaseMessageService;

    /**
     * FCM 토큰 등록 및 업데이트
     *
     * @param principal 사용자 인증 정보
     * @param fcmDto    fcm 토큰
     * @return 저장 or 업데이트 응답 메세지
     */
    @PostMapping
    public ResponseEntity<CommonResponse<?>> registerFcmToken(
            Principal principal,
            @RequestBody FcmDto fcmDto
    ) {
        Long memberId = PrincipalUtils.extractMemberId(principal);
        boolean isNewToken = firebaseMessageService.saveOrUpdateToken(memberId, fcmDto.fcmToken());
        if (isNewToken) {
            return CommonResponse.created("FCM 토큰이 성공적으로 저장되었습니다.").toResponseEntity();
        }
        return CommonResponse.updated("FCM 토큰이 업데이트 되었습니다.").toResponseEntity();

    }

    /**
     * FCM 메세지 전송 (플랫폼 공통)
     *
     * @param principal              사용자 인증 정보
     * @param notificationRequest 메세지 내용
     * @return 매세지 전송 결과
     */
    @PostMapping("/send")
    public ResponseEntity<CommonResponse<?>> sendNotification(
            Principal principal,
            @RequestBody NotificationRequest notificationRequest
    ) {
        Long memberId = PrincipalUtils.extractMemberId(principal);
        firebaseMessageService.sendNotification(memberId, notificationRequest);
        return CommonResponse.ok("앱 푸시 알림이 성공적으로 전송되었습니다.").toResponseEntity();
    }
}
