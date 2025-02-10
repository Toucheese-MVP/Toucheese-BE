package com.toucheese.member.controller;

import com.toucheese.global.data.SuccessResponse;
import com.toucheese.global.util.PrincipalUtils;
import com.toucheese.member.dto.MemberTokenResponse;
import com.toucheese.member.dto.LoginResponse;
import com.toucheese.member.dto.ReissueRequest;
import com.toucheese.member.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import com.toucheese.global.data.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/v1/tokens")
@Tag(name = "토큰 API")
public class TokenController implements TokenApi {

    private final TokenService tokenService;

    /**
     * 토큰 재발급을 처리 API
     *
     * @param reissueRequest 로그인 검증을 위한 RefreshToken, DeviceId 정보
     * @return 재발급 된 AccessToken 및 로그인 정보
     */
    @PostMapping("/reissue")
    public ResponseEntity<LoginResponse> reissueToken(
            @RequestBody @Valid ReissueRequest reissueRequest
    ) {
        MemberTokenResponse memberTokenResponse = tokenService.reissueAccessToken(reissueRequest);
        return SuccessResponse.accessTokenResponse(
                LoginResponse.of(memberTokenResponse),
                memberTokenResponse.tokenDTO().accessToken()
        );
    }

    /**
     * 로그아웃 API
     *
     * @param principal 인증된 사용자
     * @param deviceId 디바이스 ID
     * @return 로그아웃 처리
     */
    @DeleteMapping("/logout")
    public ResponseEntity<?> logout(Principal principal, @RequestParam String deviceId) {
        Long memberId = PrincipalUtils.extractMemberId(principal);
        tokenService.logout(memberId, deviceId);
        return SuccessResponse.deletedSuccess("로그아웃이 완료되었습니다.");
    }


}
