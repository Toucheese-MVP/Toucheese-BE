package com.toucheese.reservation.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.toucheese.product.dto.ProductAddOptionRequest;
import com.toucheese.product.entity.Product;
import com.toucheese.reservation.entity.Reservation;

import lombok.Builder;

@Builder
public record ReservationRequest(
	Long id,
	Long productId,
	Long studioId,
	Integer totalPrice,
	String name,
	String phone,
	LocalDate createDate,
	Integer personnel,
	List<ProductAddOptionRequest> addOptions
) {

	public static ReservationRequest of(Reservation reservation) {
		return ReservationRequest.builder()
			.id(reservation.getId())
			.productId(reservation.getProduct().getId())
			.studioId(reservation.getStudio().getId())
			.totalPrice(reservation.getTotalPrice())
			.name(reservation.getName())
			.phone(reservation.getPhone())
			.createDate(reservation.getCreateDate())
			.personnel(reservation.getPersonnel())
			.addOptions(reservation.getReservationProductAddOptions() != null ?
				reservation.getReservationProductAddOptions().stream()
					.map(option -> new ProductAddOptionRequest(option.getProductAddOption().getId(), option.getAddPrice()))
					.collect(Collectors.toList()) : List.of())
			.build();
	}

	public boolean isValidAddOption(Product product) {
		return addOptions.stream()
			.allMatch(addOptionRequest ->
				product.getProductAddOptions().stream()
					.anyMatch(productAddOption -> productAddOption.getId().equals(addOptionRequest.id())));
	}
}
