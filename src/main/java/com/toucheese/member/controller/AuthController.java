package com.toucheese.member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.toucheese.global.data.ErrorResponse;
import com.toucheese.global.util.PrincipalUtils;
import com.toucheese.member.dto.AppleAuthRequest;
import com.toucheese.member.service.AppleAuthService;
import com.toucheese.member.dto.*;
import com.toucheese.member.service.MemberService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.toucheese.global.data.SuccessResponse;
import com.toucheese.member.service.KakaoAuthService;

import io.swagger.v3.oas.annotations.Operation;
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
public class AuthController {
	private final MemberService memberService;
	private final KakaoAuthService kakaoAuthService;
	private final AppleAuthService appleAuthService;

	/**
	 * 카카오 로그인 요청 처리
	 *
	 * @param socialLoginRequest 클라이언트에서 전달된 카카오 토큰 정보
	 * @return 사용자 정보 및 JWT
	 */
	@Operation(summary = "카카오 로그인 처리", description = """
			카카오 OAuth 인증 후 전달받은 Access Token, id Token으로 사용자 정보를 조회후 사용자 정보와 JWT 토큰 발급하여 반환합니다. \n
			JWT Access Token은 Response Header로 반환합니다.""")
	@PostMapping("/kakao")
	public ResponseEntity<SocialLoginResponse> kakaoLogin(@Valid @RequestBody SocialLoginRequest socialLoginRequest) {
		SocalLoginCombinedResponse socalLoginCombinedResponse = kakaoAuthService.handleKakaoLogin(socialLoginRequest);

		return SuccessResponse.accessTokenResponse(
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

		return SuccessResponse.accessTokenResponse(
				socalLoginCombinedResponse.socialLoginResponse(),
				socalLoginCombinedResponse.accessToken());
	}


	@DeleteMapping("/kakao/withdraw")
	@Operation(summary = "[카카오] 회원 탈퇴", description = "요청 시 헤더에 access token 필요")
	public ResponseEntity<?> withdrawKakaoMember(Principal principal, @RequestParam String accessToken) throws IOException {
		Long memberId = PrincipalUtils.extractMemberId(principal);
		if (kakaoAuthService.unlink(accessToken)) {
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
	@Operation(
			summary = "애플 로그인 처리",
			description = """
					애플 OAuth 인증 후 전달받은 id Token으로 사용자 정보를 추출후 사용자 정보와 JWT 토큰 발급하여 반환합니다. \n
					JWT Access Token은 Response Header로 반환합니다."""
	)
	@ApiResponses({
			@ApiResponse(
					responseCode = "200",
					description = "[Apple Login] 로그인 성공",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = SocialLoginResponse.class)
					)
			),
			@ApiResponse(
					responseCode = "4008",
					description = "[Apple Login] ID 토큰이 유효하지 않습니다. (statusCode 는 400 입니다)",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(value = "{\n  \"success\": false,\n  \"payload\": null,\n  \"error\": {\n    \"code\": 4008,\n    \"message\": \"ID 토큰이 유효하지 않습니다.\"\n  }\n}")
					)
			),
			@ApiResponse(
					responseCode = "4009",
					description = "[Apple Login] IdToken 헤더 파싱에 실패했습니다. (statusCode 는 400 입니다)",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(value = "{\n  \"success\": false,\n  \"payload\": null,\n  \"error\": {\n    \"code\": 4009,\n    \"message\": \"IdToken 헤더 파싱에 실패했습니다.\"\n  }\n}")
					)
			)
	})
	@PostMapping("/apple")
	public ResponseEntity<SocialLoginResponse> appleLogin(@Valid @RequestBody AppleAuthRequest appleAuthRequest)
			throws AuthenticationException, NoSuchAlgorithmException, InvalidKeySpecException, JsonProcessingException {
		SocalLoginCombinedResponse socalLoginCombinedResponse = appleAuthService.handleAppleLogin(appleAuthRequest);

		return SuccessResponse.accessTokenResponse(
				socalLoginCombinedResponse.socialLoginResponse(),
				socalLoginCombinedResponse.accessToken());
	}

	/**
	 * 애플 로그인 회원 탈퇴
	 * @param principal 인증정보
	 * @param authorizationCode Authorization Code
	 * @return String 및 에러코드
	 */
	@DeleteMapping("/apple/withdraw")
	@Operation(summary = "[애플] 회원 탈퇴", description = "요청 시 헤더에 access token 필요")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "애플 회원 탈퇴가 완료되었습니다.",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponse.class))),
			@ApiResponse(responseCode = "400", description = "애플 액세스 토큰이 유효하지 않거나 탈퇴 처리 실패",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "500", description = "서버 오류: 애플 서버 통신 실패",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ResponseEntity<?> withdrawAppleMember(Principal principal, @RequestParam String authorizationCode) throws IOException {
		Long memberId = PrincipalUtils.extractMemberId(principal);
		if (appleAuthService.revoke(authorizationCode)) {
			memberService.deleteMember(memberId);
		}
		return SuccessResponse.deletedSuccess("애플 회원 탈퇴가 완료되었습니다.");
	}

}
