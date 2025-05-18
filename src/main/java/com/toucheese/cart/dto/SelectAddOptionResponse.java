package com.toucheese.cart.dto;

import com.toucheese.reservation.entity.ReservationProductAddOption;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Builder;

@Builder
public record SelectAddOptionResponse(
	Long selectOptionId,             //
	String selectOptionName,       // 옵션 이름
	Integer selectOptionPrice,     // 옵션 가격
	@Schema(description = "인원당 추가 옵션 개수 (standard가 2인 상품의 경우)", example = "2")
	Integer addOptPerPerson
) {

	public static SelectAddOptionResponse of(ReservationProductAddOption reservationProductAddOption) {
		return builder()
			.selectOptionId(reservationProductAddOption.getId())
			.selectOptionName(reservationProductAddOption.getProductAddOption().getAddOption().getAddOptionName())
			.selectOptionPrice(reservationProductAddOption.getAddPrice())
			.addOptPerPerson(reservationProductAddOption.getReservation().getAddOptPerPerson())
			.build();
	}
}