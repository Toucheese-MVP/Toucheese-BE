package com.toucheese.reservation.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toucheese.global.exception.ToucheeseBadRequestException;
import com.toucheese.product.entity.Product;
import com.toucheese.product.repository.ProductRepository;
import com.toucheese.reservation.dto.ReservationRequest;
import com.toucheese.reservation.entity.Reservation;
import com.toucheese.reservation.service.ReservationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {
	private final ReservationService reservationService;
	private final ProductRepository productRepository;

	@PostMapping("")
	public ResponseEntity<ReservationRequest> reservationCreate(@RequestBody ReservationRequest reservationRequest) {
		String name = reservationRequest.name();
		String phone = reservationRequest.phone();
		if (name.isBlank() || phone.length() < 10 || phone.length() > 13) {
			throw new ToucheeseBadRequestException("Name or Phone is missing");
		}
		Reservation reservation = reservationService.createReservation(reservationRequest);

		Product product = productRepository.findById(reservationRequest.productId())
			.orElseThrow(() -> new ToucheeseBadRequestException("Product not found"));

		if (!reservationRequest.isValidAddOption(product)) {
			throw new ToucheeseBadRequestException("Invalid AddOption for the selected product");
		}

		return ResponseEntity.status(HttpStatus.CREATED).body(ReservationRequest.of(reservation));
	}
}
