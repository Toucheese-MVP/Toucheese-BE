package com.toucheese.favorites.service;

import com.toucheese.favorites.dto.FavoritesStudioListDto;
import com.toucheese.favorites.entity.Favorites;
import com.toucheese.favorites.repository.FavoritesRepository;
import com.toucheese.member.entity.Member;
import com.toucheese.member.repository.MemberRepository;
import com.toucheese.studio.entity.Studio;
import com.toucheese.studio.repository.StudioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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
        List<Favorites> favorites = favoritesRepository.findByMemberId(memberId);
        return convertToDtoList(favorites);
    }

    // 즐겨찾기 목록 조회용 메서드 - 리팩토링1
    private List<FavoritesStudioListDto> convertToDtoList(List<Favorites> favorites){
        return favorites.stream()
                .map(this::convertToDto)
                .toList();
    }

    // 즐겨찾기 목록 조회용 메서드 - 리팩토링2
    private FavoritesStudioListDto convertToDto(Favorites favorites){
        return new FavoritesStudioListDto(favorites.getStudio();
    }

    // 스튜디오 즐겨찾기 추가용 메서드
    public void addFavorites(Long memberId, Long studioId) {
        validateFavorites(memberId, studioId);

        Member member = findMemberById(memberId);
        Studio studio = findStudioById(studioId);

        Favorites favorites = createFavorites(member, studio);
        favoritesRepository.save(favorites);
    }

    // 스튜디오 즐겨찾기 추가용 메서드 - 리팩토링1
    private void validateFavorites(Long memberId, Long studioId){
        if(favoritesRepository.existsByMemberIdAndStudioId(memberId, studioId)){
            throw new IllegalStateException("이미 즐겨찾기한 스튜디오입니다.");
        }
    }

    // 스튜디오 즐겨찾기 추가용 메서드 - 리팩토링2
    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당 회원을 찾을 수 없습니다."));
    }

    // 스튜디오 즐겨찾기 추가용 메서드 - 리팩토링3
    private Studio findStudioById(Long studioId) {
        return studioRepository.findById(studioId)
                .orElseThrow(() -> new EntityNotFoundException("해당 스튜디오를 찾을 수 없습니다."));
    }

    // 스튜디오 즐겨찾기 추가용 메서드 - 리팩토링4
    private Favorites createFavorites(Member member, Studio studio){
        return Favorites.builder()
                .member(member)
                .studio(studio)
                .build();
    }

    // 스튜디오의 즐겨찾기 취소용
    @Transactional
    public void deleteFavorites(Long memberId, Long studioId){
        validateFavoritesExistence(memberId, studioId);
        deleteFavorite(memberId, studioId);
    }

    // 스튜디오의 즐겨찾기 취소용 - 리팩토링1
    private void validateFavoritesExistence(Long memberId, Long studioId){
        if(!favoritesRepository.existsByMemberIdAndStudioId(memberId, studioId)){
            throw new IllegalStateException("즐겨찾기한 기록이 없습니다.");
        }
    }

    // 스튜디오의 즐겨찾기 취소용 - 리팩토링2
    private void deleteFavorite(Long memberId, Long studioId){
        favoritesRepository.deleteByMemberIdAndStudioId(memberId, studioId);
    }
}