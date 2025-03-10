package com.toucheese.favorites.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

public record FavoritesStudioRequestDto(
        // FavoritesStudioReqDto에서 각 필드는 회원 ID와 스튜디오 ID를 포함하며, 생성 일시를 기록함.

        Long memberId, // 즐겨찾기를 요청하는 회원의 고유 ID
        Long studioId, // 즐겨찾기할 스튜디오의 고유 ID

        // 요청이 생성된 시간
        // JSON 직렬화 시 이 필드는 무시됨.
        // 즉, 이 필드는 클라이언트에게 전송되지 않음.
        @JsonIgnore
        LocalDateTime createAt
) {
    // 회원 ID와 스튜디오 ID를 인자로 받아 DTO 객체를 생성하는 생성자
    public FavoritesStudioRequestDto(Long memberId, Long studioId){
        this(
                memberId,
                studioId,
                LocalDateTime.now()
        );// createdAt을 기본값으로 설정
          // createAt 필드를 현재 시간으로 초기화
          // 즉, 사용자가 즐겨찾기 요청을 할 때의 시간을 자동으로 기록
    }
}
