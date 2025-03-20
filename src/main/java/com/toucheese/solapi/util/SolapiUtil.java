package com.toucheese.solapi.util;

import com.toucheese.global.exception.ToucheeseBadRequestException;
import com.toucheese.global.exception.ToucheeseInternalServerErrorException;
import com.toucheese.member.entity.Member;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.checkerframework.checker.index.qual.SameLen;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Slf4j
@Component
public class SolapiUtil {
    @Value("${solapi.from-number}")
    private String fromNumber; // 고정 발신 번호

    private final DefaultMessageService solapiService;

    public static final String MESSAGE_TEMPLATE = "안녕하세요, %s 님 ! 예약 접수되었습니다.";
    public SolapiUtil(DefaultMessageService solapiService) {
        this.solapiService = solapiService;
    }

    public String formatMessage(String name) {
        return String.format(MESSAGE_TEMPLATE, name);
    }

    public String determineFormatMessage(String name) {
        return String.format("안녕하세요, %s 님! 예약이 확정되었습니다.", name);
    }

    public String cancelFormatMessage(String name) {
        return String.format("안녕하세요, %s 님! 예약이 취소되었습니다.", name);
    }

    public void send(String phone, String messageText) {
        Message message = new Message();
        message.setFrom(fromNumber);
        message.setTo(phone);
        message.setText(messageText);

        try {
            solapiService.send(message);
        } catch (NurigoMessageNotReceivedException exception) {
            throw new ToucheeseBadRequestException("Faild messages: " + exception.getFailedMessageList());
        } catch (Exception exception) {
            throw new ToucheeseInternalServerErrorException("An unexpected error occurred:" + exception.getMessage());
        }
    }
}
