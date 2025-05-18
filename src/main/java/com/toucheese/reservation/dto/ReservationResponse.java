package com.toucheese.reservation.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.toucheese.cart.dto.SelectAddOptionResponse;
import com.toucheese.reservation.entity.Reservation;
import com.toucheese.reservation.entity.ReservationStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "예약 조회 응답")
public record ReservationResponse(
        @Schema(description = "예약 ID", example = "1")
        Long reservationId,
        @Schema(description = "스튜디오 ID", example = "1")
        Long studioId,
        @Schema(description = "스튜디오 이름", example = "포토스튜디오")
        String studioName,
        @Schema(description = "스튜디오 이미지 URL", example = "https://example.com/studio.jpg")
        String studioImage,
        @Schema(description = "상품 ID", example = "1")
        Long productId,
        @Schema(description = "상품 이름", example = "기본 패키지")
        String productName,
        @Schema(description = "예약 날짜", example = "2024-03-21")
        LocalDate createDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        @Schema(type = "string", description = "예약 시간", example = "19:00")
        LocalTime createTime,
        @Schema(description = "인원당 추가 옵션 개수 (standard가 2인 상품의 경우)", example = "2")
        Integer addOptPerPerson,
        @Schema(description = "선택된 추가 옵션 목록", example = """
                [
                    {
                        "selectOptionId": 1,
                        "selectOptionName": "헤어",
                        "selectOptionPrice": 10000
                    }
                ]
                """)
        List<SelectAddOptionResponse> selectAddOptions,
        @Schema(description = "예약 상태", example = "예약접수")
        ReservationStatus status
) {
    public static ReservationResponse of(Reservation reservation, String baseUrl) {
        return builder()
                .reservationId(reservation.getId())
                .studioId(reservation.getStudio().getId())
                .studioName(reservation.getStudio().getName())
                .studioImage(baseUrl + reservation.getStudio().getProfileImage())
                .productId(reservation.getProduct().getId())
                .productName(reservation.getProduct().getName())
                .createDate(reservation.getCreateDate())
                .createTime(reservation.getCreateTime())
                .addOptPerPerson(reservation.getAddOptPerPerson())
                .selectAddOptions(reservation.getReservationProductAddOptions().stream()
                        .map(SelectAddOptionResponse::of)
                        .toList())
                .status(reservation.getStatus())
                .build();
    }
}
