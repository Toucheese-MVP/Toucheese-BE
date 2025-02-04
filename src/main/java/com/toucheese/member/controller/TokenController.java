package com.toucheese.member.controller;

import com.toucheese.global.data.SuccessResponse;
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
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/v1/tokens")
@Tag(name = "토큰 API")
public class TokenController {

    private final TokenService tokenService;

    /**
     * 토큰 재발급을 처리하는 API
     * @param reissueRequest 로그인 검증을 위한 RefreshToken, DeviceId 정보
     * @return 재발급 된 AccessToken 및 로그인 정보
     */
    @PostMapping("/reissue")
    @Operation(summary = "토큰 재발급", description = "만료된 AccessToken을 재발급 합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "재발급된 토큰 반환",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = LoginResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Refresh 토큰 만료",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(value = "{\n  \"success\": false,\n  \"payload\": null,\n  \"error\": {\n    \"code\": 4001,\n    \"message\": \"Refresh 토큰이 만료되었습니다, 재로그인이 필요합니다.\"\n  }\n}")
                            )
                    ),

                    @ApiResponse(
                            responseCode = "404",
                            description = "deviceId에 해당하는 토큰을 찾을 수 없음",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(value = "{\n  \"success\": false,\n  \"payload\": null,\n  \"error\": {\n    \"code\": 4004,\n    \"message\": \"deviceId에 해당하는 토큰을 찾을 수 없습니다.\"\n  }\n}")
                            )
                    )
            }
    )
    public ResponseEntity<LoginResponse> reissueToken(
            @RequestBody @Valid ReissueRequest reissueRequest
    ) {
        MemberTokenResponse memberTokenResponse = tokenService.reissueAccessToken(reissueRequest);
        return SuccessResponse.accessTokenResponse(
                LoginResponse.of(memberTokenResponse),
                memberTokenResponse.tokenDTO().accessToken()
        );
    }
}
