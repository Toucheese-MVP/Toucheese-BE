package com.toucheese.firebase.service;


import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.toucheese.firebase.dto.FcmMessageRequest;
import com.toucheese.firebase.entity.FcmToken;
import com.toucheese.firebase.repository.FcmTokenRepository;
import com.toucheese.global.exception.ErrorCode;
import com.toucheese.global.exception.GlobalCustomException;
import com.toucheese.member.repository.MemberRepository;
import com.toucheese.member.service.MemberService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;


@Service
@Slf4j
@RequiredArgsConstructor
public class FirebaseMessageService {
    private final MemberService memberService;

    private final FcmTokenRepository fcmTokenRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void saveOrUpdateToken(Long memberId, String fcmToken) {
        Optional<FcmToken> optFcmToken = fcmTokenRepository.findByMemberId(memberId);
        // fcm 토큰이 저장되어있으면 -> 업데이트
        if (optFcmToken.isPresent()) {
            FcmToken targetFcm = optFcmToken.get();
            targetFcm.setFcmToken(fcmToken);
            fcmTokenRepository.save(targetFcm);
        }
        // fcm 토큰이 없으면 -> 생성
        else {
            fcmTokenRepository.save(FcmToken.builder()
                    .member(memberRepository.findById(memberId)
                            .orElseThrow(() -> new GlobalCustomException(ErrorCode.MEMBER_NOT_FOUND))
                    )
                    .fcmToken(fcmToken)
                    .build());
        }
    }

    @Async
    public CompletableFuture<String> sendMessage(FcmMessageRequest requestDto) {
        return CompletableFuture.supplyAsync(() -> {
            FcmToken memberFcmToken = fcmTokenRepository.findByMemberId(requestDto.memberId())
                    .orElseThrow(() -> new GlobalCustomException(ErrorCode.FCM_NOT_FOUND));

            Message message = Message.builder()
                    .putData("title", requestDto.title())
                    .putData("content", requestDto.body())
                    .setToken(memberFcmToken.getFcmToken())
                    .build();

            try {
                return "Message sent successfully: " + FirebaseMessaging.getInstance().send(message);
            } catch (FirebaseMessagingException e) {
                log.error("FCM 메시지 전송 실패: memberId={}, error={}", requestDto.memberId(), e.getMessage(), e);
                return "Failed to send message";
            }
        });

    }

}
