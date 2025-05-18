package com.toucheese.reservation.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.toucheese.global.exception.ErrorCode;
import com.toucheese.global.exception.GlobalCustomException;
import com.toucheese.member.entity.Member;
import com.toucheese.member.repository.MemberRepository;
import com.toucheese.member.service.MemberService;
import com.toucheese.reservation.dto.ReservationRequest;
import com.toucheese.reservation.dto.ReservationSuccessResponse;
import com.toucheese.solapi.util.SolapiUtil;
import com.toucheese.studio.service.StudioService;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.toucheese.cart.entity.Cart;
import com.toucheese.global.util.CsvUtils;
import com.toucheese.product.entity.ProductAddOption;
import com.toucheese.product.service.ProductService;
import com.toucheese.reservation.dto.ReservationUpdateRequest;
import com.toucheese.reservation.entity.Reservation;
import com.toucheese.reservation.entity.ReservationProductAddOption;
import com.toucheese.reservation.entity.ReservationStatus;
import com.toucheese.reservation.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

	private final ReservationRepository reservationRepository;
	private final ReservationReadService reservationReadService;
	private final StudioService studioService;
	private final ProductService productService;
	private final MemberRepository memberRepository;
	private final SolapiUtil solapiUtil;
	// private final FcmService fcmService;

	@Transactional
	public void createReservationsFromCarts(List<Cart> carts) {
		List<Reservation> reservations = carts.stream().map(cart -> {
			List<Long> addOptionIds = CsvUtils.fromCsv(cart.getAddOptions());

			List<ProductAddOption> productAddOptions = productService.findProductAddOptionsByProductIdAndAddOptionIds(
				cart.getProduct().getId(), addOptionIds
			);

			Reservation reservation = Reservation.builder()
				.product(cart.getProduct())
				.studio(cart.getStudio())
				.member(cart.getMember())
				.totalPrice(cart.getTotalPrice())
				.createDate(cart.getCreateDate())
				.createTime(cart.getCreateTime())
				.personnel(cart.getPersonnel())
				.status(ReservationStatus.예약접수)
				.reservationCompletedAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
				.build();

			List<ReservationProductAddOption> reservationProductAddOptions = productAddOptions.stream()
				.map(productAddOption -> new ReservationProductAddOption(productAddOption,
					productAddOption.getAddOptionPrice(), reservation))
				.collect(Collectors.toList());

			reservation.setReservationProductAddOptions(reservationProductAddOptions);
			return reservation;
		}).collect(Collectors.toList());

		reservationRepository.saveAll(reservations);
	}

	@Transactional
	public void updateReservation(Long memberId, Long reservationId, ReservationUpdateRequest request) {
		Reservation reservation = reservationReadService.findReservationByIdAndMemberId(reservationId, memberId);

		reservation.updateReservationStatusAndTime(request);
	}

	// 즉시 예약 메서드(iOS용)
	@Transactional
	public ReservationSuccessResponse createInstantReservation(Long memberId, ReservationRequest reservationRequest) {
		// 요청된 회원 ID 로그
		logMemberId(memberId);

		// 회원 정보 조회 및 전화번호 업데이트
		Member member = getMemberAndUpdatePhone(memberId, reservationRequest.phone());

		// 예약 객체 생성
		Reservation reservation = Reservation.builder()
				.product(productService.findProductById(reservationRequest.productId()))
				.studio(studioService.findStudioById(reservationRequest.studioId()))
				.member(member)
				.phone(member.getPhone())
				.totalPrice(reservationRequest.totalPrice())
				.createDate(reservationRequest.createDate())
				.createTime(reservationRequest.createTime())
				.personnel(reservationRequest.personnel())
				.addOptPerPerson(reservationRequest.addOptPerPerson())
				.status(ReservationStatus.예약접수)
				.reservationCompletedAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
				.build();

		// 상품 추가 옵션 조회 및 설정
		getReservationProductAddOptions(reservationRequest, reservation);

		// 예약 저장 및 문자 메시지 전송
		reservationRepository.save(reservation);
		sendReservationMessage(member);

		return ReservationSuccessResponse.builder()
				.status(true)
				.build();
	}

	// 즉시 예약 메서드(iOS용) - 리팩토링(실행 시 로그에 회원 ID가 잘 나오는지 확인하기 위함.)
	private void logMemberId(Long memberId) {
		System.out.println("회원 ID: " + memberId);
	}

	// 즉시 예약 메서드(iOS용) - 리팩토링
	// 회원 ID와 회원의 전화번호를 찾는 메서드
	private Member getMemberAndUpdatePhone(Long memberId, String requestPhone) {
		// 회원 ID 조회
		Optional<Member> memberOpt = memberRepository.findById(memberId);

		// 회원 ID가 없을 시 -> 예외 처리
		if (memberOpt.isEmpty()) {
			throw new GlobalCustomException(ErrorCode.MEMBER_NOT_FOUND);
		}

		// 회원 ID 조회
		Member member = memberOpt.get();

		// 회원 데이터에 회원의 전화번호가 비어 있는지를 확인하고, 비어 있다면 요청된 전화번호로 업데이트
		if (!StringUtils.hasText(member.getPhone())) {
			updateMemberPhone(member, requestPhone);
		}

		// 회원 반환
		return member;
	}

	// 즉시 예약 메서드(iOS용) - 리팩토링
	// 회원의 전화번호를 업데이트하는 메서드
	private void updateMemberPhone(Member member, String requestPhone) {
		// 전화번호 유효성 검사
		if (StringUtils.hasText(requestPhone)) {
			// 전화번호 업데이트
			log.info("{}님의 전화번호가 {}로 업데이트 되었습니다.", member.getName(), requestPhone);
			member.setPhone(requestPhone);
			memberRepository.save(member);
		} else { // 전화번호가 유효하지 않은 경우
			log.info("요청에 전화번호 필드가 null 이거나 값이 비어있습니다.");
			throw new GlobalCustomException(ErrorCode.PHONE_REQUEST_NOT_FOUND);
		}
	}

	// 즉시 예약 메서드(iOS용) - 리팩토링
	// 예약 요청에 포함된 추가 옵션을 기반으로 예약 상품 추가 옵션 목록을 생성하는 메서드
	private List<ReservationProductAddOption> getReservationProductAddOptions(ReservationRequest reservationRequest, Reservation reservation) {
		// 상품 추가 옵션 조회
		List<ProductAddOption> productAddOptions = productService.findProductAddOptionsByProductIdAndAddOptionIds(
				reservationRequest.productId(), reservationRequest.addOptions()
		);

		// 예약 상품 추가 옵션 변환
		List<ReservationProductAddOption> reservationProductAddOptions = productAddOptions.stream()
				.map(productAddOption -> new ReservationProductAddOption(
						productAddOption,
						productAddOption.getAddOptionPrice(),
						reservation
				))
				.collect(Collectors.toList());

		reservation.setReservationProductAddOptions(reservationProductAddOptions);
		return reservationProductAddOptions;
	}

	// 즉시 예약 메서드(iOS용) - 리팩토링
	// 예약 완료 후 회원에게 문자 메시지를 전송하는 메서드
	private void sendReservationMessage(Member member) {
		String messageText = solapiUtil.formatMessage(member.getName()); // 메시지 텍스트 생성
		solapiUtil.send(member.getPhone(), messageText); // 문자 메시지 전송
	}

	/*
	 * 알림 로직
	 * memberId - 푸시 메시지 받아야 하는 멤버 아이디
	 * reservation - studio 이름을 얻기 위한 것이므로 Studio, studioName 등으로 수정 가능
	 * pushMsg - 메시지 종류 PushMsg ENUM 참고 -> 예약 성공: PushMsg.RESERVATION_SUCCEED, 예약 실패: PushMsg.RESERVATION_FAILED
	 */
	// 푸시 알림을 보내는 메서드
//	public void sendPushMsg(Long memberId, Reservation reservation, PushMsg pushMsg) {
		// 해당 사용자 디바이스 토큰을 Redis에서 가져옵니다
//		String deviceToken = deviceService.getDeviceToken(memberId);
//
//		// 만약 토큰이 존재하면 푸시 메시지를 전송합니다
//		if (deviceToken != null) {
//			try {
//				fcmService.sendPushMsg(
//						deviceToken,
//						pushMsg,
//						reservation.getStudio().getName()
//				);
//			} catch (IOException | FirebaseMessagingException e) {
//				throw new RuntimeException(e);
//			}
//		} else {
//			// 토큰이 없으면 알림을 보내지 않음
//			// 필요한 경우 로깅 또는 다른 처리를 할 수 있습니다.
//			log.info("Device token not found, push notification not sent.");
//		}
//	}


	//		// 회원의 전화번호 유무 확인
//		if (member.getPhone() == null || member.getPhone().isEmpty()) {
//			;
//		}

	//		// 예약 목록 조회
//		List<Reservation> reservations = reservationRepository.findByMemberId(memberId);
//
//		// 예약이 없을 경우 예외 처리
//		if (reservations.isEmpty()) {
//			throw new ToucheeseJwtException(ErrorCode.RESERVATION_NOT_FOUND);
//		}

	//				.productId(reservation.getProduct().getId())
//				.studioId(reservation.getStudio().getId())
//				.memberId(reservation.getMember().getId())
//				.totalPrice(reservation.getTotalPrice())
//				.createDate(reservation.getCreateDate())
//				.createTime(reservation.getCreateTime())
//				.personnel(reservation.getPersonnel())
//				.addOptions(reservationProductAddOptions.stream()
//						.map(option -> option.getProductAddOption().getId())
//						.collect(Collectors.toList()))

	// System.out.println("예약 수: " + reservations.size());

//		reservations.stream()
//				.filter(reservation -> {
//					boolean hasPhone = reservation.getPhone() != null && !reservation.getPhone().trim().isEmpty();
//					if (!hasPhone) {
//						System.out.println("전화번호가 없는 예약 ID: " + reservation.getId());
//					}
//					return hasPhone;
//				})
//				.findFirst();



//		// 전화번호가 있는 예약을 찾기
//		Optional<Reservation> reservationWithPhone = reservations.stream()
//				.filter(reservation -> reservation.getPhone() != null && !reservation.getPhone().trim().isEmpty())
//				.findFirst();

//		// 전화번호가 있는 예약이 없을 경우 예외 처리
//		if (reservationWithPhone.isEmpty()) {
//			throw new ToucheeseJwtException(ErrorCode.DONT_HAVE_PHONE_NUMBER);
//		}

	// System.out.println("저장할 전화번호: " + reservationRequest.phone());
}