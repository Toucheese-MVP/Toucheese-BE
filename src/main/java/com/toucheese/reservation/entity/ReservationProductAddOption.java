package com.toucheese.reservation.entity;

import com.toucheese.product.entity.ProductAddOption;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationProductAddOption {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Integer addPrice;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_add_option_id", nullable = false)
	private ProductAddOption productAddOption;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reservation_id", nullable = false)
	private Reservation reservation;

	public ReservationProductAddOption(ProductAddOption productAddOption, Integer addPrice, Reservation reservation) {
		this.productAddOption = productAddOption;
		this.addPrice = addPrice;
		this.reservation = reservation;
	}
}
