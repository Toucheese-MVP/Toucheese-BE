package com.toucheese.firebase.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.toucheese.firebase.dto.AndroidNotificationRequest;
import com.toucheese.firebase.dto.iOSNotificationRequest;
import com.toucheese.global.exception.ErrorCode;
import com.toucheese.global.exception.GlobalCustomException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FirebaseUtils {

    private final ObjectMapper objectMapper;

    public FirebaseUtils() {
        this.objectMapper = new ObjectMapper();
    }

    private String convertMapToJson(Map<String, String> data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new GlobalCustomException(ErrorCode.FCM_JSON_CONVERT_FAILED);
        }
    }

    public void sendIosMessage(String fcmToken, iOSNotificationRequest iOSNotificationRequest) {
        Message message = Message.builder()
                .putData("alert", iOSNotificationRequest.alert())
                .putData("badge", String.valueOf(iOSNotificationRequest.badge()))
                .putData("sound", iOSNotificationRequest.sound())
                .putData("custom_data", convertMapToJson(iOSNotificationRequest.customData()))
                .setToken(fcmToken)
                .build();
        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            throw new GlobalCustomException(ErrorCode.FCM_SEND_FAILED);
        }
    }

    public void sendAndroidMessage(String fcmToken, AndroidNotificationRequest androidNotificationRequest) {
        Message message = Message.builder()
                .putData("title", androidNotificationRequest.title())
                .putData("body", androidNotificationRequest.body())
                .putData("data", convertMapToJson(androidNotificationRequest.data()))
                .setToken(fcmToken)
                .build();
        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            throw new GlobalCustomException(ErrorCode.FCM_SEND_FAILED);
        }
    }
}
