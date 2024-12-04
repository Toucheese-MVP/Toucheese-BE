package com.toucheese.product.dto;

import com.toucheese.product.entity.ProductAddOption;

import lombok.Builder;

@Builder
public record ProductAddOptionRequest(
	Long id
) {
	public static ProductAddOptionRequest of(ProductAddOption productAddOption) {
		return ProductAddOptionRequest.builder()
			.id(productAddOption.getId())
			.build();
	}
}
