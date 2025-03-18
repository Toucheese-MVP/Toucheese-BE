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


    public String sendMessage(Long memberId, FcmMessageRequest fcmMessageRequest) {
        FcmToken memberFcmToken = fcmTokenRepository.findByMemberId(memberId)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.FCM_NOT_FOUND));

        Message message = Message.builder()
                .putData("title", fcmMessageRequest.title())
                .putData("content", fcmMessageRequest.body())
                .setToken(memberFcmToken.getFcmToken())
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            return "Message sent sucessfully: " + response;
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
            return "Failed to send message";
        }
    }

}
