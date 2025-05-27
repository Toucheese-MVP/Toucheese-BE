package com.toucheese.product.dto;

import java.util.List;
import java.util.Set;

import com.toucheese.product.entity.Product;

import lombok.Builder;

@Builder
public record ProductDetailResponse(
	Long id,
	String name,
	String description,
	String productImage,
	Integer reviewCount,
	Integer standard,
	Integer price,
	List<AddOptionResponse> addOptions,
	PlusOptionInfo plusOptionInfo
) {
	private static final Set<Long> PLUS_OPTION_IDS = Set.of(61L, 80L, 93L, 97L, 109L, 137L, 142L, 143L, 163L, 168L, 171L, 189L, 190L, 197L, 202L, 211L);

	public static ProductDetailResponse of(Product product, String baseUrl) {
		List<AddOptionResponse> addOptions = product.getProductAddOptions().stream()
			.map(AddOptionResponse::of)
			.toList();

		// plus 옵션 찾기
		AddOptionResponse plusOption = addOptions.stream()
			.filter(option -> PLUS_OPTION_IDS.contains(option.id()))
			.findFirst()
			.orElse(null);

		PlusOptionInfo plusOptionInfo = PlusOptionInfo.builder()
			.isPlusOpt(plusOption != null ? 1 : 0)
			.plusOptPrice(plusOption != null ? plusOption.price() : 0)
			.build();

		return ProductDetailResponse.builder()
			.id(product.getId())
			.name(product.getName())
			.description(product.getDescription())
			.productImage(baseUrl + product.getProductImage())
			.reviewCount(product.getReviews().size())
			.standard(product.getStandard())
			.price(product.getPrice())
			.addOptions(addOptions)
			.plusOptionInfo(plusOptionInfo)
			.build();
	}
}
