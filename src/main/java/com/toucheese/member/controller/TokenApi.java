package com.toucheese.member.controller;

import com.toucheese.global.data.ErrorResponse;
import com.toucheese.global.data.SuccessResponse;
import com.toucheese.member.dto.LoginResponse;
import com.toucheese.member.dto.ReissueRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

public interface TokenApi {
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
    ResponseEntity<LoginResponse> reissueToken(@RequestBody @Valid ReissueRequest reissueRequest);

    @DeleteMapping("/logout")
    @Operation(
            summary = "회원 로그아웃",
            description = "요청 시 헤더에 access token 필요",
            responses =  {
                    @ApiResponse(
                            responseCode = "200",
                            description = "성공적으로 로그아웃되었습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "로그아웃에 실패하였습니다.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject("\n  \"success\": false,\n  \"payload\": null,\n  \"error\": {\n    \"code\": 4017,\n    \"message\": \"로그아웃에 실패하였습니다.\"\n  }\n")
                            )
                    )
            }
    )
    ResponseEntity<?> logout(Principal principal, @RequestParam String deviceId);
}
