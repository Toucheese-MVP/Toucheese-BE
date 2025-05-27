package com.toucheese.product.dto;

import com.toucheese.product.entity.ProductAddOption;

import lombok.Builder;
import java.util.Set;

@Builder
public record AddOptionResponse(
	Long id,
	Integer price,
	String name,
	Integer isPlusOpt
) {
	private static final Set<Long> PLUS_OPTION_IDS = Set.of(61L, 80L, 93L, 97L, 109L, 137L, 142L, 143L, 163L, 168L, 171L);

	public static AddOptionResponse of(ProductAddOption productAddOption) {
		return AddOptionResponse.builder()
			.id(productAddOption.getAddOption().getId())
			.name(productAddOption.getAddOption().getAddOptionName())
			.price(productAddOption.getAddOptionPrice())
			.isPlusOpt(PLUS_OPTION_IDS.contains(productAddOption.getAddOption().getId()) ? 1 : 0)
			.build();
	}
}
