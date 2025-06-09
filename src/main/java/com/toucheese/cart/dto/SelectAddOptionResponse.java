package com.toucheese.cart.dto;

import com.toucheese.reservation.entity.ReservationProductAddOption;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Builder;

@Builder
public record SelectAddOptionResponse(
	@Schema(description = "선택한 옵션의 ID", example = "1")
	Long selectOptionId,             // 옵션 ID
	@Schema(description = "선택한 옵션의 이름", example = "헤어")
	String selectOptionName,       // 옵션 이름
	@Schema(description = "선택한 옵션의 가격", example = "10000")
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