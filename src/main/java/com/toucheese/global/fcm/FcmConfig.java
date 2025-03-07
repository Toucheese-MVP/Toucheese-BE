//package com.toucheese.global.fcm;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

//@Slf4j
//@Configuration
//public class FcmConfig {
//    @Bean
//    public FirebaseApp firebaseApp() throws IOException {
//        InputStream serviceAccountStream = new ClassPathResource("firebase/firebase_service_key.json")
//                .getInputStream();
//
//        // ClassPathResource("firebase/firebase_servicekey.json")
//    }
//}
//            // Firebase 앱이 초기화 되지 않은 경우에 강제 초기화
//            if (FirebaseApp.getApps().isEmpty()) {
//                FirebaseApp.initializeApp(options);
//            }
//
//            log.info("firebase config 설정 성공");
//        }

//        FirebaseOptions options = FirebaseOptions.builder()
//                .setCredentials(GoogleCredentials.fromStream(refreshToken))
//                .setDatabaseUrl("https://<DATABASE_NAME>.firebaseio.com/")
//                .build();
//
//
//        FirebaseApp.initializeApp(options);

// }

// json 키 파일 존재 여부 확인
//        if(!resource.exists()) {
//            throw new IOException("!!! Firebase service key file not found at path !!!");
//        }

//        // 파일을 InputStream으로 직접 로드
//        try (InputStream serviceAccount = resource.getInputStream()) {
//            FirebaseOptions options = FirebaseOptions.builder()
//                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                    .build();
