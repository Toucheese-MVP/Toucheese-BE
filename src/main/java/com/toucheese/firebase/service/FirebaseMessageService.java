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
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class FirebaseMessageService {
    private final MemberService memberService;
    private final FirebaseMessaging firebaseMessaging;
    private final FcmTokenRepository fcmTokenRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public boolean saveOrUpdateToken(Long memberId, String fcmToken) {
        return fcmTokenRepository.findByMemberId(memberId)
                .map(existingToken -> {
                    existingToken.setFcmToken(fcmToken); // ✅ 기존 토큰 업데이트
                    return false; // 기존 토큰이 있어서 업데이트만 수행
                })
                .orElseGet(() -> {
                    fcmTokenRepository.save(FcmToken.builder()
                            .member(memberRepository.getReferenceById(memberId))
                            .fcmToken(fcmToken)
                            .build());
                    return true; // ✅ 새로운 토큰이 생성됨
                });
    }


    public String sendMessage(Long memberId, FcmMessageRequest fcmMessageRequest) {
        FcmToken memberFcmToken = fcmTokenRepository.findByMemberId(memberId)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.FCM_NOT_FOUND));

        Message message = Message.builder()
                .putData("title", fcmMessageRequest.title())
                .putData("content", fcmMessageRequest.body())
                .setToken(memberFcmToken.getFcmToken())
                .build();

        try {
            return FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            throw new GlobalCustomException(ErrorCode.FCM_SEND_FAILED);
        }
    }

}
