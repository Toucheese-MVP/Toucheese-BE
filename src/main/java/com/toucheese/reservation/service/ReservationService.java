package com.toucheese.reservation.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.toucheese.global.exception.ErrorCode;
import com.toucheese.global.exception.ToucheeseJwtException;
import com.toucheese.member.service.MemberService;
import com.toucheese.reservation.dto.ReservationRequest;
import com.toucheese.reservation.dto.ReservationSuccessResponse;
import com.toucheese.studio.repository.StudioRepository;
import com.toucheese.studio.service.StudioService;
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

@Service
@RequiredArgsConstructor
public class ReservationService {

	private final ReservationRepository reservationRepository;
	private final ReservationReadService reservationReadService;
	private final StudioService studioService;
	private final ProductService productService;
	private final MemberService memberService;

	@Transactional
	public void createReservationsFromCarts(List<Cart> carts) {
		List<Reservation> reservations = carts.stream().map(cart -> {
			List<Long> addOptionIds = CsvUtils.fromCsv(cart.getAddOptions());

			List<ProductAddOption> productAddOptions = productService.findProductAddOptionsByProductIdAndAddOptionIds(
				cart.getProduct().getId(), addOptionIds
			);

			List<ReservationProductAddOption> reservationProductAddOptions = productAddOptions.stream()
				.map(productAddOption -> new ReservationProductAddOption(productAddOption,
					productAddOption.getAddOptionPrice()))
				.collect(Collectors.toList());

			return Reservation.builder()
				.product(cart.getProduct())
				.studio(cart.getStudio())
				.member(cart.getMember())
				.totalPrice(cart.getTotalPrice())
				.createDate(cart.getCreateDate())
				.createTime(cart.getCreateTime())
				.personnel(cart.getPersonnel())
				.reservationProductAddOptions(reservationProductAddOptions)
				.status(ReservationStatus.예약접수)
				.reservationCompletedAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
				.build();
		}).collect(Collectors.toList());

		reservationRepository.saveAll(reservations);
	}

	@Transactional
	public void updateReservation(Long memberId, Long reservationId, ReservationUpdateRequest request) {
		Reservation reservation = reservationReadService.findReservationByIdAndMemberId(reservationId, memberId);

		reservation.updateReservationStatusAndTime(request);
	}

	@Transactional
	public ReservationSuccessResponse createInstantReservation(Long memberId, ReservationRequest reservationRequest) {

		List<ProductAddOption> productAddOptions = productService.findProductAddOptionsByProductIdAndAddOptionIds(
				reservationRequest.productId(), reservationRequest.addOptions()
		);

		List<ReservationProductAddOption> reservationProductAddOptions =
				productAddOptions.stream()
						.map(productAddOption -> new ReservationProductAddOption(
								productAddOption,
								productAddOption.getAddOptionPrice()
						))
						.collect(Collectors.toList());

		// 회원 ID에 대한 예약 목록 조회
		List<Reservation> reservations = reservationRepository.findByMemberId(memberId);

		// 예약이 없을 경우 예외 처리
		if (reservations.isEmpty()) {
			throw new ToucheeseJwtException(ErrorCode.DONT_HAVE_RESERVATION);
		}

		// 전화번호가 있는 예약을 찾기
		Optional<Reservation> reservationWithPhone = reservations.stream()
				.filter(reservation -> reservation.getPhone() != null && !reservation.getPhone().isEmpty())
				.findFirst();

		// 전화번호가 있는 예약이 없을 경우 예외 처리
		if (reservationWithPhone.isEmpty()) {
			throw new ToucheeseJwtException(ErrorCode.DONT_HAVE_PHONE_NUMBER);
		}

		// 새로운 예약 생성
		Reservation reservation = Reservation.builder()
				.product(productService.findProductById(reservationRequest.productId()))
				.studio(studioService.findStudioById(reservationRequest.studioId()))
				.member(memberService.findMemberById(memberId))
				.phone(reservationRequest.phone())
				.totalPrice(reservationRequest.totalPrice())
				.createDate(reservationRequest.createDate())
				.createTime(reservationRequest.createTime())
				.personnel(reservationRequest.personnel())
				.reservationProductAddOptions(reservationProductAddOptions)
				.status(ReservationStatus.예약접수)
				.reservationCompletedAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
				.build();

		reservationRepository.save(reservation);

		return ReservationSuccessResponse.builder()
				.status(true)
				.build();
	}

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
}