package com.toucheese.cart.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record CartUpdateRequest (
	Integer totalPrice,
	Integer personnel,
	@Schema(description = "인원당 추가 옵션 개수 (standard가 2인 상품의 경우)", example = "2")
	Integer addOptPerPerson,
	List<Long> addOptions
) {}
