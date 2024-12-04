package com.toucheese.reservation.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.toucheese.product.entity.Product;
import com.toucheese.product.entity.ProductAddOption;
import com.toucheese.product.service.ProductService;
import com.toucheese.reservation.dto.ReservationRequest;
import com.toucheese.reservation.entity.Reservation;
import com.toucheese.reservation.entity.ReservationProductAddOption;
import com.toucheese.reservation.repository.ReservationRepository;
import com.toucheese.studio.entity.Studio;
import com.toucheese.studio.service.StudioService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {

	private final ReservationRepository reservationRepository;
	private final StudioService studioService;
	private final ProductService productService;
	private final ReservationValidationService reservationValidationService;

	@Transactional
	public Reservation createReservation(ReservationRequest reservationRequest) {
		Product product = productService.findProductById(reservationRequest.productId());

		Studio studio = studioService.findStudioById(reservationRequest.studioId());

		reservationValidationService.validateAddOptionsForProduct(reservationRequest, product);

		List<ReservationProductAddOption> reservationProductAddOptions = reservationRequest.addOptions().stream()
			.map(addOptionRequest -> {
				ProductAddOption productAddOption = productService.findProductAddOptionById(addOptionRequest.id());
				return new ReservationProductAddOption(productAddOption, productAddOption.getAddOptionPrice());
			})
			.collect(Collectors.toList());

		Reservation reservation = Reservation.builder()
			.product(product)
			.studio(studio)
			.totalPrice(reservationRequest.totalPrice())
			.name(reservationRequest.name())
			.phone(reservationRequest.phone())
			.createDate(reservationRequest.createDate())
			.createTime(reservationRequest.createTime())
			.personnel(reservationRequest.personnel())
			.reservationProductAddOptions(reservationProductAddOptions)
			.build();

		return reservationRepository.save(reservation);
	}
}
