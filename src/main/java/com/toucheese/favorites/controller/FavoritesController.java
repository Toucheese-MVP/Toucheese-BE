package com.toucheese.favorites.controller;

import com.toucheese.favorites.dto.FavoritesStudioListDto;
import com.toucheese.favorites.dto.FavoritesStudioRequestDto;
import com.toucheese.favorites.service.FavoritesService;
import com.toucheese.global.response.Response;
import com.toucheese.global.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/favorites", produces = APPLICATION_JSON_VALUE)
public class FavoritesController {
    private final FavoritesService favoritesService;



    /**
     * 즐겨찾기한 스튜디오 목록 조회
     */
    @Operation(
            summary = "즐겨찾기한 스튜디오 목록 조회 [헤더 토큰 필요]",
            description = "회원 ID를 사용하여 즐겨찾기한 스튜디오 목록을 조회합니다."
    )
    @GetMapping
    public Response<List<FavoritesStudioListDto>> getFavoriteStudios(@RequestParam Long memberId) {
        List<FavoritesStudioListDto> FavoriteStudios = FavoritesService.getFavoriteStudios(memberId);
        return Response.of(SuccessCode.GET_FAVORITES_STUDIOS_SUCCESS, FavoriteStudios);
    }

    /**
     * 스튜디오 즐겨찾기
     */
    @Operation(
            summary = "스튜디오 즐겨찾기 [헤더 토큰 필요]",
            description = "회원 ID와 스튜디오 ID를 사용하여 특정 스튜디오를 즐겨찾기 목록에 추가합니다."
    )
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public Response<Void> addFavoritesStudio(@RequestBody FavoritesStudioRequestDto favoritesStudioRequestDto) {
        favoritesService.addFavorites(favoritesStudioRequestDto.memberId(), favoritesStudioRequestDto.studioId());
        return Response.of(SuccessCode.ADD_FAVORITES_SUCCESS);
    }

    /**
     * 스튜디오 즐겨찾기 취소
     */
    @Operation(
            summary = "스튜디오 즐겨찾기 취소 [헤더 토큰 필요]",
            description = "회원 ID와 스튜디오 ID를 사용하여 즐겨찾기 목록에서 특정 스튜디오를 삭제합니다."
    )
    @DeleteMapping("/{studioId}/deleteFavorites")
    public Response<Void> deleteFavoriteStudio(
            @RequestParam Long memberId,
            @PathVariable Long studioId
    ){
        favoritesService.deleteFavorites(Long memberId, Long studioId);
        return Response.of(SuccessCode.REMOVE_FAVORITES_SUCCESS);
    }
}
