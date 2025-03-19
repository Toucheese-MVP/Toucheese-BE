package com.toucheese.firebase.utils;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.toucheese.firebase.dto.FcmMessageRequest;
import com.toucheese.global.exception.ErrorCode;
import com.toucheese.global.exception.GlobalCustomException;
import org.springframework.stereotype.Component;

@Component
public class FirebaseUtils {
    public void sendMessage(String fcmToken, FcmMessageRequest fcmMessageRequest) {
        Message message = Message.builder()
                .putData("title", fcmMessageRequest.title())
                .putData("content", fcmMessageRequest.body())
                .setToken(fcmToken)
                .build();
        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            throw new GlobalCustomException(ErrorCode.FCM_SEND_FAILED);
        }
    }
}
