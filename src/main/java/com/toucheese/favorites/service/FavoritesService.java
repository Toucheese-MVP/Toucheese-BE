package com.toucheese.favorites.service;

import com.toucheese.favorites.dto.FavoritesStudioListDto;
import com.toucheese.favorites.entity.Favorites;
import com.toucheese.favorites.repository.FavoritesRepository;
import com.toucheese.member.entity.Member;
import com.toucheese.member.repository.MemberRepository;
import com.toucheese.studio.entity.Studio;
import com.toucheese.studio.repository.StudioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoritesService {
    private final FavoritesRepository favoritesRepository;
    private final MemberRepository memberRepository;
    private final StudioRepository studioRepository;

    // 즐겨찾기 목록 조회용 메서드
    public List<FavoritesStudioListDto> getFavoriteStudios(Long memberId) {
        return favoritesRepository.findByMemberId(memberId).stream()
                .map(favorites -> new FavoritesStudioListDto(favorites.getStudio()))
                .toList();
    }

    // 스튜디오 즐겨찾기 추가용 메서드
    public void addFavorites(Long memberId, Long studioId) {
        if(favoritesRepository.existsByMemberIdAndStudioId(memberId, studioId)){
            throw new IllegalStateException("이미 즐겨찾기한 스튜디오입니다.");
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당 회원을 찾을 수 없습니다."));

        Studio studio = studioRepository.findById(studioId)
                .orElseThrow(() -> new EntityNotFoundException("해당 스튜디오를 찾을 수 없습니다."));

        Favorites favorites = Favorites.builder()
                .member(member)
                .studio(studio)
                .build();

        favoritesRepository.save(favorites);
    }

    // 스튜디오의 즐겨찾기 취소용
    public void deleteFavorites(Long memberId, Long studioId){
        if(!favoritesRepository.existsByMemberIdAndStudioId(memberId, studioId)){
            throw new IllegalStateException("즐겨찾기한 기록이 없습니다.");
        }

        favoritesRepository.deleteByMemberIdAndStudioId(memberId, studioId);

    }
}