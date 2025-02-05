package com.toucheese.reservation.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationSuccessResponse {
    // 상품
    private Long productId;
    // 스튜디오
    private Long studioId;
    // 회원
    private Long memberId;
    // 총 가격
    private Integer totalPrice;
    // 예약 날짜 && 시간
    private LocalDate createDate;

    private LocalTime createTime;
    // 인원
    private Integer personnel;
    // 옵션
    private List<Long> addOptions;
    // 성공 여부
    private boolean status;
}
