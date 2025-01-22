package com.toucheese.reservation.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.toucheese.reservation.entity.Reservation;
import com.toucheese.reservation.entity.ReservationProductAddOption;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ReservationRequest(
        @Schema(description = "상품 ID", example = "1")
		Long productId,
		@Schema(description = "스튜디오 ID", example = "1")
        Long studioId,
		@Schema(description = "회원 ID", example = "1")
        Long memberId,
		@Schema(description = "총 가격", example = "100000")
        Integer totalPrice,
		@Schema(description = "예약 날짜", example = "2025-01-21")
        LocalDate createDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        @Schema(type = "string", example = "19:00")
        LocalTime createTime,
		@Schema(description = "인원 수", example = "2")
        Integer personnel,
        @Schema(description = "추가 옵션 ID 목록", example = "[1, 2]")
        List<Long> addOptions
) {

    public static ReservationRequest of(Reservation reservation) {
        return ReservationRequest.builder()
                .productId(reservation.getProduct().getId())
                .studioId(reservation.getStudio().getId())
                .memberId(reservation.getMember().getId())
                .totalPrice(reservation.getTotalPrice())
                .createDate(reservation.getCreateDate())
                .createTime(reservation.getCreateTime())
                .personnel(reservation.getPersonnel())
                .addOptions(reservation.getReservationProductAddOptions() != null ?
                        reservation.getReservationProductAddOptions().stream()
                                .map(ReservationProductAddOption::getId)
                                .collect(Collectors.toList()) : Collections.emptyList())
                .build();
    }
}
