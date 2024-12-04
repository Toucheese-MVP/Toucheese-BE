package com.toucheese.reservation.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.toucheese.global.exception.ToucheeseBadRequestException;
import com.toucheese.product.entity.Product;
import com.toucheese.product.entity.ProductAddOption;
import com.toucheese.product.service.ProductService;
import com.toucheese.reservation.dto.ReservationRequest;
import com.toucheese.reservation.entity.ReservationProductAddOption;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationValidationService {

	private final ProductService productService;

	/**
	 * 상품에 해당하는 추가 옵션이 유효한지 검증한다.
	 * @param reservationRequest 예약 요청 객체
	 * @param product 상품 객체
	 * @return 유효한 경우 true, 그렇지 않으면 false
	 * @throws ToucheeseBadRequestException 유효하지 않은 AddOption이 포함되어 있을 때 발생
	 */
	public boolean isValidAddOption(ReservationRequest reservationRequest, Product product) {
		return reservationRequest.addOptions().stream()
			.allMatch(addOptionRequest ->
				product.getProductAddOptions().stream()
					.anyMatch(productAddOption -> productAddOption.getId().equals(addOptionRequest.id())));
	}

	/**
	 * 예약 요청에 포함된 AddOption들을 상품에 맞게 변환하여 리스트로 반환한다.
	 * @param reservationRequest 예약 요청 객체
	 * @param product 상품 객체
	 * @return 변환된 ReservationProductAddOption 리스트
	 * @throws ToucheeseBadRequestException 존재하지 않는 AddOption이나 유효하지 않은 AddOption이 포함되어 있을 때 발생
	 */
	public List<ReservationProductAddOption> toReservationProductAddOptions(
		ReservationRequest reservationRequest, Product product
	) {
		return reservationRequest.addOptions().stream()
			.map(addOptionRequest -> {
				ProductAddOption productAddOption = productService.findProductAddOptionById(addOptionRequest.id());
				return new ReservationProductAddOption(productAddOption, addOptionRequest.addOptionPrice());
			})
			.collect(Collectors.toList());
	}

	/**
	 * 예약 요청에 포함된 AddOption들이 상품에 맞는지 검증한다.
	 * @param reservationRequest 예약 요청 객체
	 * @param product 상품 객체
	 * @throws ToucheeseBadRequestException 유효하지 않은 AddOption이 포함되어 있을 때 발생
	 */
	public void validateAddOptionsForProduct(ReservationRequest reservationRequest, Product product) {
		boolean isValid = isValidAddOption(reservationRequest, product);
		if (!isValid) {
			throw new ToucheeseBadRequestException("Invalid AddOption for the selected product");
		}
	}
}
