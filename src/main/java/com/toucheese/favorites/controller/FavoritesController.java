package com.toucheese.favorites.controller;

import com.toucheese.favorites.dto.FavoritesStudioListDto;
import com.toucheese.favorites.dto.FavoritesStudioRequestDto;
import com.toucheese.favorites.service.FavoritesService;
import com.toucheese.global.response.Response;
import com.toucheese.global.response.SuccessCode;
import com.toucheese.global.util.PrincipalUtils;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/favorites", produces = APPLICATION_JSON_VALUE)
public class FavoritesController {
    private final FavoritesService favoritesService;

    /**
     * 즐겨찾기한 스튜디오 목록 조회
     */
    @Operation(
            summary = "즐겨찾기한 스튜디오 목록 조회 [헤더 토큰 필요]",
            description = "회원 ID를 사용하여 즐겨찾기한 스튜디오 목록을 조회합니다."
    )
    @GetMapping("/inquiry")
    public Response<List<FavoritesStudioListDto>> getFavoriteStudios(
            @RequestParam Long memberId
    ) {
        List<FavoritesStudioListDto> favoriteStudios = favoritesService.getFavoriteStudios(memberId);
        return Response.of(SuccessCode.GET_FAVORITES_STUDIOS_SUCCESS, favoriteStudios);
    }

    /**
     * 스튜디오 즐겨찾기 추가
     */
    @Operation(
            summary = "스튜디오 즐겨찾기 [헤더 토큰 필요]",
            description = "회원 ID와 스튜디오 ID를 사용하여 특정 스튜디오를 즐겨찾기 목록에 추가합니다."
    )
    @GetMapping("/addFavorites")
    public Response<Void> addFavoritesStudio(
            Principal principal,
            @RequestParam Long studioId
    ) {
        Long memberId = PrincipalUtils.extractMemberId(principal);
        favoritesService.addFavorites(memberId, studioId);
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
        favoritesService.deleteFavorites(memberId, studioId);
        return Response.of(SuccessCode.REMOVE_FAVORITES_SUCCESS);
    }
}
