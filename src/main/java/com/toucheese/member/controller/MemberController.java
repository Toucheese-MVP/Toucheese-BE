package com.toucheese.member.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.toucheese.global.data.SuccessResponse;
import com.toucheese.global.util.PrincipalUtils;
import com.toucheese.member.dto.FindEmailRequest;
import com.toucheese.member.dto.LoginRequest;
import com.toucheese.member.dto.LoginResponse;
import com.toucheese.member.dto.MemberFirstLoginUpdateRequest;
import com.toucheese.member.dto.MemberResponse;
import com.toucheese.member.dto.MemberTokenResponse;
import com.toucheese.member.dto.ResetPasswordRequest;
import com.toucheese.member.dto.SignupRequest;
import com.toucheese.member.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/members")
@Tag(name = "회원 API")
public class MemberController {
    private final MemberService memberService;

    /**
     * 로그인 요청을 처리하는 API
     * @param loginRequest 로그인 요청 정보 (email, password, deviceId)
     * @return 로그인 시 생성 된 접근 토큰 및 로그인 정보
     */
    @PostMapping
    @Operation(summary = "회원 로그인", description = "email, password로 로그인 합니다.")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        MemberTokenResponse memberTokenResponse = memberService.login(loginRequest);
        return SuccessResponse.accessTokenResponse(
                LoginResponse.of(memberTokenResponse),
                memberTokenResponse.tokenDTO().accessToken()
        );
    }

    @PutMapping
    @Operation(summary = "첫 로그인 회원 정보 변경")
    public ResponseEntity<?> memberFirstLoginUpdate(@RequestBody @Valid MemberFirstLoginUpdateRequest request, Principal principal) {
        memberService.memberFirstLoginUpdate(request, principal);
        return SuccessResponse.updatedSuccess("회원 정보를 성공적으로 업데이트했습니다.");
    }

    @GetMapping
    @Operation(summary = "내 정보 불러오기")
    public ResponseEntity<MemberResponse> getMemberInfo(Principal principal) {
        Long memberId = PrincipalUtils.extractMemberId(principal);
        return SuccessResponse.getObjectSuccess(
                MemberResponse.of(memberService.findMemberById(memberId))
        );
    }

    @PostMapping("/signup")
    @Operation(summary = "회원 가입")
    public ResponseEntity<?> signup(@RequestBody @Valid SignupRequest signupRequest) {

        memberService.createSignup(signupRequest);
        return SuccessResponse.createdSuccess("회원 가입이 완료되었습니다.");
    }

    @DeleteMapping
    @Operation(summary = "회원 탈퇴")
    public ResponseEntity<?> deleteMember(Principal principal) {
        Long memberId = PrincipalUtils.extractMemberId(principal);

        memberService.deleteMember(memberId);
        return SuccessResponse.deletedSuccess("회원 탈퇴가 완료되었습니다.");
    }


    @DeleteMapping("/cleanup")
    @Operation(
        summary = "임시 회원 탈퇴 (회원 데이터 삭제)",
        description = "회원과 관련된 데이터(장바구니, 예약, 리뷰, 문의 글, 토큰)를 삭제합니다.\n" +
                "회원 자체는 삭제되지 않으며, 회원이 소셜 로그인으로 가입한 경우에 한하여 해당 데이터를 정리합니다."
    )
    public ResponseEntity<?> cleanupMemberData(Principal principal) {
        Long memberId = PrincipalUtils.extractMemberId(principal);

        memberService.deleteMemberRelatedData(memberId);
        return SuccessResponse.deletedSuccess("회원 데이터 삭제가 완료되었습니다.");
    }


    @GetMapping("/email")
    @Operation(summary = "이메일 찾기")
    public ResponseEntity<String> findEmail(@RequestBody @Valid FindEmailRequest findEmailRequest) {

        String email = memberService.findEmail(findEmailRequest);
        return SuccessResponse.getObjectSuccess(email);
    }

    @PutMapping("/password")
    @Operation(summary = "비밀번호 변경")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {
        memberService.resetPassword(resetPasswordRequest);
        return SuccessResponse.updatedSuccess("비밀번호가 변경되었습니다.");
    }
}