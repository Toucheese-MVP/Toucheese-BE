package com.toucheese.firebase.service;



import com.toucheese.firebase.dto.AndroidNotificationRequest;
import com.toucheese.firebase.dto.iOSNotificationRequest;
import com.toucheese.firebase.entity.FcmToken;
import com.toucheese.firebase.repository.FcmTokenRepository;
import com.toucheese.firebase.utils.FirebaseUtils;
import com.toucheese.global.exception.ErrorCode;
import com.toucheese.global.exception.GlobalCustomException;
import com.toucheese.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class FirebaseMessageService {
    private final FcmTokenRepository fcmTokenRepository;
    private final MemberRepository memberRepository;
    private final FirebaseUtils firebaseUtils;

    @Transactional
    public boolean saveOrUpdateToken(Long memberId, String fcmToken) {
        return fcmTokenRepository.findByMemberId(memberId)
                .map(existingToken -> {
                    existingToken.setFcmToken(fcmToken);
                    return false;
                })
                .orElseGet(() -> {
                    fcmTokenRepository.save(FcmToken.builder()
                            .member(memberRepository.getReferenceById(memberId))
                            .fcmToken(fcmToken)
                            .build());
                    return true; 
                });
    }


    public void sendIosNotification(Long memberId, iOSNotificationRequest iOSNotificationRequest) {
        FcmToken memberFcmToken = fcmTokenRepository.findByMemberId(memberId)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.FCM_NOT_FOUND));

        firebaseUtils.sendIosMessage(memberFcmToken.getFcmToken(), iOSNotificationRequest);
    }

    public void sendAndroidNotification(Long memberId, AndroidNotificationRequest androidNotificationRequest) {
        FcmToken memberFcmToken = fcmTokenRepository.findByMemberId(memberId)
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.FCM_NOT_FOUND));

        firebaseUtils.sendAndroidMessage(memberFcmToken.getFcmToken(), androidNotificationRequest);
    }

}
