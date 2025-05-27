package com.toucheese.product.dto;

import lombok.Builder;

@Builder
public record PlusOptionInfo(
	Integer isPlusOpt,
	Integer plusOptPrice
) {
	public static PlusOptionInfoBuilder builder() {
		return new PlusOptionInfoBuilder();
	}

	public static class PlusOptionInfoBuilder {
		private Integer isPlusOpt;
		private Integer plusOptPrice;

		public PlusOptionInfoBuilder isPlusOpt(Integer isPlusOpt) {
			this.isPlusOpt = isPlusOpt;
			return this;
		}

		public PlusOptionInfoBuilder plusOptPrice(Integer plusOptPrice) {
			this.plusOptPrice = plusOptPrice;
			return this;
		}

		public PlusOptionInfo build() {
			return new PlusOptionInfo(isPlusOpt, plusOptPrice);
		}
	}
} 