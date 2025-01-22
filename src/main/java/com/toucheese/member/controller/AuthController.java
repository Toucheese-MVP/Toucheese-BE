package com.toucheese.member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.toucheese.member.dto.AppleAuthRequest;
import com.toucheese.member.service.AppleAuthService;
import com.toucheese.member.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.toucheese.global.data.ApiResponse;
import com.toucheese.member.service.KakaoAuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.naming.AuthenticationException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
@Tag(name = "인증 API")
public class AuthController {
	private final KakaoAuthService kakaoAuthService;
	private final AppleAuthService appleAuthService;

	/**
	 * 카카오 로그인 요청 처리
	 * @param socialLoginRequest 클라이언트에서 전달된 카카오 토큰 정보
	 * @return 사용자 정보 및 JWT
	 */
	@Operation(summary = "카카오 로그인 처리", description = """
	카카오 OAuth 인증 후 전달받은 Access Token, id Token으로 사용자 정보를 조회후 사용자 정보와 JWT 토큰 발급하여 반환합니다. \n
	JWT Access Token은 Response Header로 반환합니다.""")
	@PostMapping("/kakao")
	public ResponseEntity<SocialLoginResponse> kakaoLogin(@Valid @RequestBody SocialLoginRequest socialLoginRequest) {
		SocalLoginCombinedResponse socalLoginCombinedResponse = kakaoAuthService.handleKakaoLogin(socialLoginRequest);

		return ApiResponse.accessTokenResponse(
			socalLoginCombinedResponse.socialLoginResponse(),
			socalLoginCombinedResponse.accessToken());
	}
	@Operation(summary = "카카오 로그인 콜백 처리", description = """
	카카오에서 리다이렉트된 인증 코드(`code`)를 받아 Access Token, id Token을 얻고 사용자 정보를 조회 후 사용자 정보와 JWT 토큰을 반환합니다. \n
	JWT Access Token은 Response Header로 반환합니다.""")
	@GetMapping("/kakao/callback")
	public ResponseEntity<SocialLoginResponse> kakaoCallback(@RequestParam String code) {
		SocialLoginRequest socialLoginRequest = kakaoAuthService.getAccessTokenFromKakao(code);

		SocalLoginCombinedResponse socalLoginCombinedResponse = kakaoAuthService.handleKakaoLogin(socialLoginRequest);

		return ApiResponse.accessTokenResponse(
			socalLoginCombinedResponse.socialLoginResponse(),
			socalLoginCombinedResponse.accessToken());
	}

	/**
	 * 애플 로그인 요청 처리
	 * @param appleAuthRequest 클라이언트에서 전달된 애플 토큰 정보
	 * @return 사용자 정보 및 JWT
	 */
	@Operation(summary = "애플 로그인 처리", description = """
	애플 OAuth 인증 후 전달받은 id Token으로 사용자 정보를 추출후 사용자 정보와 JWT 토큰 발급하여 반환합니다. \n
	JWT Access Token은 Response Header로 반환합니다.""")
	@PostMapping("/apple")
	public ResponseEntity<SocialLoginResponse> appleLogin(@Valid @RequestBody AppleAuthRequest appleAuthRequest)
			throws AuthenticationException, NoSuchAlgorithmException, InvalidKeySpecException, JsonProcessingException
	{
		SocalLoginCombinedResponse socalLoginCombinedResponse = appleAuthService.handleAppleLogin(appleAuthRequest);

		return ApiResponse.accessTokenResponse(
				socalLoginCombinedResponse.socialLoginResponse(),
				socalLoginCombinedResponse.accessToken());
	}
}
