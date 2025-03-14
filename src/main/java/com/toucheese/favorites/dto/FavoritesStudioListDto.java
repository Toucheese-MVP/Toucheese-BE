package com.toucheese.favorites.dto;

import com.toucheese.image.entity.StudioImage;
import com.toucheese.studio.entity.Studio;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

public record FavoritesStudioListDto(
        Long id, // 스튜디오의 고유 ID
        String name, // 스튜디오의 이름
        String profileImage, // 스튜디오의 로고 이미지
        Float rating, // 스튜디오의 별점
        List<StudioImage> studioImages, // 스튜디오의 이미지 리스트
        Integer price, // 스튜디오의 가격
        int reviewCount // 스튜디오의 리뷰 개수
) {
    // Studio를 인자로 받아 Studio의 속성을 기반으로 FavoritesStudioListDto 객체를 생성하는 생성자
    // Studio의 다양한 메서드를 호출해 DTO의 각 필드를 초기화하기 위해 필요
    // 클라이언트로 스튜디오 정보를 전송하기 위해 사용
    public FavoritesStudioListDto(Studio studio) {
                studio.getId(), // 스튜디오의 고유 ID를 가져옴
                studio.getName(), // 스튜디오명을 가져옴
                studio.getProfileImage(), // 스튜디오의 로고 이미지를 가져옴
                studio.getRating(), // 스튜디오의 별점을 가져옴
                studio.getStudioImages().stream()
                        .map(studioImage -> new StudioImage(studio, studioImage.getResizedPath()))
                        .collect(Collectors.toList(),
                studio.getPrice(), // 스튜디오의 가격을 가져옴
                studio.getReviews().size(); // 리뷰 개수를 가져옴
    }
}

