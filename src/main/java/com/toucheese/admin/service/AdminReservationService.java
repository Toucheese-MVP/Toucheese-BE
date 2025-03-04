package com.toucheese.admin.service;

import java.time.LocalDate;
import java.util.Optional;

import com.toucheese.global.exception.ErrorCode;
import com.toucheese.global.exception.GlobalCustomException;
import com.toucheese.member.entity.Member;
import com.toucheese.member.repository.MemberRepository;
import com.toucheese.reservation.repository.ReservationRepository;
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
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminReservationService {

	private final ReservationReadService reservationReadService;
	private final ReservationRepository reservationRepository;
	private final SolapiUtil solapiUtil;

	@Transactional(readOnly = true)
	public Page<AdminReservationListResponse> findReservations(ReservationStatus status, LocalDate createDate,
		int page) {
		Pageable pageable = PageUtils.createPageable(page);

		return reservationReadService.findReservationsByStatusAndDate(status, createDate, pageable)
			.map(AdminReservationListResponse::of);
	}

	@Transactional
	public void updateReservationStatus(Long reservationId, ReservationStatus newStatus) {
		// 예약 정보를 조회
		Reservation reservation = reservationReadService.findReservationById(reservationId);

		// 예약이 존재하지 않을 경우 예외 처리
		if(reservation == null ){
			throw new GlobalCustomException(ErrorCode.RESERVATION_NOT_FOUND);
		}

		// 상태가 null인지 확인
		if (newStatus == null) {
			log.error("새로운 상태가 null입니다.");
			throw new GlobalCustomException(ErrorCode.RESERVATION_STATUS_NOT_FOUND);
		}

		// 예약 상태가 "예약 확정"일 경우 문자 메시지 전송
		if(newStatus == ReservationStatus.예약확정){
			String messageText = solapiUtil.determineFormatMessage(reservation.getMember().getName());
			String registeredSenderNumber = "01098455844";
			solapiUtil.send(registeredSenderNumber, reservation.getMember().getPhone(), messageText);
		}

		// 예약 상태 업데이트
		reservation.updateStatus(newStatus);

		// 변경 사항 저장
		reservationRepository.save(reservation);
	}
}
