package com.toucheese.member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.toucheese.global.util.PrincipalUtils;
import com.toucheese.member.dto.AppleAuthRequest;
import com.toucheese.member.service.AppleAuthService;
import com.toucheese.member.dto.*;
import com.toucheese.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.toucheese.global.data.SuccessResponse;
import com.toucheese.member.service.KakaoAuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.naming.AuthenticationException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.spec.InvalidKeySpecException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
@Tag(name = "인증 API")
public class AuthController implements AuthApi {
	private final MemberService memberService;
	private final KakaoAuthService kakaoAuthService;
	private final AppleAuthService appleAuthService;

	/**
	 * 카카오 로그인 요청 처리
	 *
	 * @param socialLoginRequest 클라이언트에서 전달된 카카오 토큰 정보
	 * @return 사용자 정보 및 JWT
	 */
	public ResponseEntity<SocialLoginResponse> kakaoLogin(@Valid @RequestBody SocialLoginRequest socialLoginRequest) {
		SocalLoginCombinedResponse socalLoginCombinedResponse = kakaoAuthService.handleKakaoLogin(socialLoginRequest);

		return SuccessResponse.accessTokenResponse(
				socalLoginCombinedResponse.socialLoginResponse(),
				socalLoginCombinedResponse.accessToken());
	}

	/**
	 * 카카오 로그인 콜백 처리
	 *
	 * @param code 카카오에서 리다이렉트된 인증 코드
	 * @return 사용자 정보와 JWT
	 */
	public ResponseEntity<SocialLoginResponse> kakaoCallback(@RequestParam String code) {
		SocialLoginRequest socialLoginRequest = kakaoAuthService.getAccessTokenFromKakao(code);

		SocalLoginCombinedResponse socalLoginCombinedResponse = kakaoAuthService.handleKakaoLogin(socialLoginRequest);

		return SuccessResponse.accessTokenResponse(
				socalLoginCombinedResponse.socialLoginResponse(),
				socalLoginCombinedResponse.accessToken());
	}

	/**
	 * 카카오 회원 탈퇴
	 *
	 * @param principal 인증된 사용자 정보
	 * @param code 카카오에서 리다이렉트된 인증 코드
	 * @return 탈퇴 처리
	 */
	public ResponseEntity<?> withdrawKakaoMember(Principal principal, @RequestParam String code) {
		Long memberId = PrincipalUtils.extractMemberId(principal);
		if (kakaoAuthService.unlink(code)) {
			memberService.deleteMember(memberId);
		}
		return SuccessResponse.deletedSuccess("카카오 회원 탈퇴가 완료되었습니다.");
	}

	/**
	 * 애플 로그인 요청 처리
	 *
	 * @param appleAuthRequest 클라이언트에서 전달된 애플 토큰 정보
	 * @return 사용자 정보 및 JWT
	 */
	@PostMapping("/apple")
	public ResponseEntity<SocialLoginResponse> appleLogin(@Valid @RequestBody AppleAuthRequest appleAuthRequest)
			throws AuthenticationException, NoSuchAlgorithmException, InvalidKeySpecException, JsonProcessingException {
		SocalLoginCombinedResponse socalLoginCombinedResponse = appleAuthService.handleAppleLogin(appleAuthRequest);

		return SuccessResponse.accessTokenResponse(
				socalLoginCombinedResponse.socialLoginResponse(),
				socalLoginCombinedResponse.accessToken());
	}

	/**
	 * 애플 회원 탈퇴
	 *
	 * @param principal 인증정보
	 * @param authorizationCode Authorization Code
	 * @return String 및 에러코드
	 */
	@DeleteMapping("/apple/withdraw")
	public ResponseEntity<?> withdrawAppleMember(Principal principal, @RequestParam String authorizationCode) throws IOException {
		Long memberId = PrincipalUtils.extractMemberId(principal);
		if (appleAuthService.revoke(authorizationCode)) {
			memberService.deleteMember(memberId);
		}
		return SuccessResponse.deletedSuccess("애플 회원 탈퇴가 완료되었습니다.");
	}

}
