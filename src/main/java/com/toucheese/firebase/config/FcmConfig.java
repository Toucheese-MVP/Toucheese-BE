package com.toucheese.firebase.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Configuration
@Slf4j
public class FcmConfig {
    @Value("${firebase.config.path}")
    String firebaseConfigPath;


    @Bean
    public FirebaseApp firebaseApp() {
        try {
            File file = new File(firebaseConfigPath);
            log.info("생성한 File 객체 경로 확인 : " + file.getAbsolutePath());
            if (!file.exists()) {
                throw new IOException("File 객체가 존재하지 않음");
            }

            FileInputStream serviceAccount =
                    new FileInputStream(firebaseConfigPath);

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            // FirebaseApp이 이미 초기화되었는지 확인
            if (FirebaseApp.getApps().isEmpty()) {
                return FirebaseApp.initializeApp(options);
            } else {
                return FirebaseApp.getInstance();
            }
        } catch (IOException e) {
            log.error("Firebase 초기화 과정 중 문제 발생: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        return FirebaseMessaging.getInstance(firebaseApp());
    }
}
