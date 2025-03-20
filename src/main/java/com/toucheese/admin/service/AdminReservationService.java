package com.toucheese.admin.service;

import java.time.LocalDate;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.toucheese.firebase.dto.NotificationRequest;
import com.toucheese.firebase.entity.FcmToken;
import com.toucheese.firebase.repository.FcmTokenRepository;
import com.toucheese.firebase.utils.FirebaseUtils;
import com.toucheese.global.exception.ErrorCode;
import com.toucheese.global.exception.GlobalCustomException;
import com.toucheese.member.entity.Member;
import com.toucheese.solapi.util.SolapiUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.toucheese.admin.dto.AdminReservationListResponse;
import com.toucheese.global.util.PageUtils;
import com.toucheese.reservation.entity.Reservation;
import com.toucheese.reservation.entity.ReservationStatus;
import com.toucheese.reservation.service.ReservationReadService;

import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminReservationService {

    private final ReservationReadService reservationReadService;
    private final SolapiUtil solapiUtil;
    private final FirebaseUtils firebaseUtils;
    private final FcmTokenRepository fcmTokenRepository;

    @Transactional(readOnly = true)
    public Page<AdminReservationListResponse> findReservations(
            ReservationStatus status,
            LocalDate createDate,
            int page
    ) {
        Pageable pageable = PageUtils.createPageable(page);

        return reservationReadService.findReservationsByStatusAndDate(status, createDate, pageable)
                .map(AdminReservationListResponse::of);
    }

    @Transactional
    public void updateReservationStatus(Long reservationId, ReservationStatus newStatus) throws FirebaseMessagingException {
        Reservation reservation = reservationReadService.findReservationById(reservationId);
        Member member = reservation.getMember();

        snedMessage(newStatus, reservation, member);
        reservation.updateStatus(newStatus); // dirty checking
    }

    private void snedMessage(ReservationStatus newStatus, Reservation reservation, Member member) throws FirebaseMessagingException {
        // 문자 메시지 전송
        String messageText = solapiUtil.determineFormatMessage(reservation.getMember().getName());
        String registeredSenderNumber = "01098455844";
        solapiUtil.send(registeredSenderNumber, reservation.getMember().getPhone(), messageText);

        // FCM 푸시 알림
        FcmToken fcmToken = fcmTokenRepository.findByMemberId(member.getId())
                .orElseThrow(() -> new GlobalCustomException(ErrorCode.FCM_NOT_FOUND));
        NotificationRequest notificationRequest = NotificationRequest.builder()
                .title(String.format("[터치즈] %s 알림", newStatus))
                .body(String.format("'%s' 예약이 '%s' 처리 되었습니다.", reservation.getStudio().getName(), newStatus))
                .build();

        firebaseUtils.sendMessage(fcmToken.getFcmToken(), notificationRequest);

    }
}
