package com.toucheese.favorites.repository;

import com.toucheese.favorites.entity.Favorites;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoritesRepository extends JpaRepository<Favorites, Long> {
    // member가 찜한 스튜디오 목록 조회
    List<Favorites> findByMemberId(Long memberId);

    // member가 특정 스튜디오를 즐겨찾기 했는지 확인
    boolean existsByStudioId(Long studioId);

    boolean existsByMemberIdAndStudioId(Long memberId, Long studioId);

    // 즐겨찾기 기록 삭제
    void deleteByMemberIdAndStudioId(Long memberId, Long studioId);
}
