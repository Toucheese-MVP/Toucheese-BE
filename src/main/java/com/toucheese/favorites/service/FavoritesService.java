package com.toucheese.favorites.service;

import com.toucheese.favorites.dto.FavoritesStudioListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoritesService {

    // 즐겨찾기 목록 조회용 메서드
    public static List<FavoritesStudioListDto> getFavoriteStudios(Long memberId) {
        return null;
    }

    // 스튜디오 즐겨찾기 추가용 메서드
    public void addFavorites(Long memberId, Long studioId) {

    }

    // 스튜디오의 즐겨찾기 취소용
    public void deleteFavorites(Long memberId, Long studioId){

    }
}
