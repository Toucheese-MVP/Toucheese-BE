package com.toucheese.cart.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;

public record CheckoutCartItemsResponse (
	@Schema(description = "장바구니 ID", example = "1")
	Long cartId,
	@Schema(description = "스튜디오 이름", example = "포토스튜디오")
	String studioName,          // 스튜디오 이름
	@Schema(description = "상품 이미지 URL", example = "https://example.com/image.jpg")
	String productImage,        // 상품이미지
	@Schema(description = "상품 이름", example = "기본 패키지")
	String productName,         // 상품 이름
	@Schema(description = "상품 가격", example = "100000")
	Integer productPrice,		// 상품 가격
	@Schema(description = "예약 인원", example = "2")
	Integer personnel,          // 예약 인원
	@Schema(description = "예약 날짜", example = "2024-03-21")
	LocalDate reservationDate,  // 예약 날짜
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
	@Schema(type = "string", description = "예약 시간", example = "19:00")
	LocalTime reservationTime,  // 예약 시간
	@Schema(description = "상품 1개 전체 가격 (상품 가격 + 선택한 옵션 가격)", example = "150000")
	Integer totalPrice,         // 상품 1개 전체 가격
	@Schema(description = "선택한 추가 옵션 정보", example = """
		[
			{
				"selectOptionId": 1,
				"selectOptionName": "헤어",
				"selectOptionPrice": 10000,
				"addOptPerPerson": 2
			}
		]
		""")
	List<SelectAddOptionResponse> selectAddOptions // 선택한 추가 옵션 정보
) {
}