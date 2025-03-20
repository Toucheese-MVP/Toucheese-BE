package com.toucheese.firebase.utils;

import com.google.firebase.messaging.FirebaseMessaging;
import com.toucheese.firebase.dto.NotificationRequest;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class FirebaseUtils {
    private final FirebaseMessaging firebaseMessaging;

    public void sendMessage(String fcmToken, NotificationRequest notificationRequest) throws FirebaseMessagingException {
        Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(notificationRequest.toNotification())
                .build();
        firebaseMessaging.send(message);
    }
}
