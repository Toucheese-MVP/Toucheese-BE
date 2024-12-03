package com.toucheese.reservation.entity;

import java.time.LocalDate;
import java.util.List;

import com.toucheese.product.entity.Product;
import com.toucheese.studio.entity.Studio;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Integer totalPrice;

	private String name;

	private String phone;

	private LocalDate createDate;

	private Integer personnel;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id")
	private Product product;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "studio_id")
	private Studio studio;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "reservation_id")
	private List<ReservationProductAddOption> reservationProductAddOptions;

	private String status;

	@PrePersist
	private void prePersist() {
		if (this.status == null) {
			this.status = "예약대기"; // 데이터 저장 전 기본 상태 설정
		}
	}

	public Reservation(Product product, Studio studio, Integer totalPrice, String name, String phone, LocalDate createDate, Integer personnel, List<ReservationProductAddOption> reservationProductAddOptions) {
		this.product = product;
		this.studio = studio;
		this.totalPrice = totalPrice;
		this.name = name;
		this.phone = phone;
		this.createDate = createDate;
		this.personnel = personnel;
		this.reservationProductAddOptions = reservationProductAddOptions;
	}

}
