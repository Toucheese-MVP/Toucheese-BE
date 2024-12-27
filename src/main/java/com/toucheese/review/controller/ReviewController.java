package com.toucheese.review.controller;

import com.toucheese.global.data.ApiResponse;
import com.toucheese.global.util.PrincipalUtils;
import com.toucheese.review.dto.ReviewDetailResponse;
import com.toucheese.review.dto.ReviewRequest;
import com.toucheese.review.dto.ReviewResponse;
import com.toucheese.review.service.ReviewQueryService;
import com.toucheese.review.service.ReviewCommandService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/studios/{studioId}")
@Tag(name = "리뷰 API", description = "스튜디오 리뷰 목록 조회, 리뷰 상세 조회, 상품별 리뷰 조회")
public class ReviewController {

    private final ReviewCommandService reviewCommandService;
    private final ReviewQueryService reviewQueryService;

    @GetMapping("/reviews")
    @Operation(summary = "스튜디오 리뷰 목록 조회", description = "리뷰 탭 클릭 시 해당 스튜디오 리뷰 전체 조회")
    public ResponseEntity<List<ReviewResponse>> getStudioReviews(@PathVariable("studioId") Long studioId) {
        return ApiResponse.getObjectSuccess(reviewQueryService.getReviewsByStudioId(studioId));
    }

    @GetMapping("/reviews/{reviewId}")
    @Operation(summary = "특정 리뷰 상세 조회", description = "리뷰 사진 클릭 시 해당 리뷰 조회")
    public ResponseEntity<ReviewDetailResponse> findStudioReviewDetail(@PathVariable("reviewId") Long reviewId) {
        return ApiResponse.getObjectSuccess(reviewQueryService.findReviewDetailById(reviewId));
    }

    @GetMapping("/products/{productId}/reviews")
    @Operation(summary = "특정 상품 리뷰 목록 조회", description = "상품 리뷰 클릭시 해당 상품 리뷰 조회")
    public ResponseEntity<List<ReviewResponse>> getProductReviews(@PathVariable("productId") Long productId) {
        return ApiResponse.getObjectSuccess(reviewQueryService.getReviewsByProductId(productId));
    }

    @PostMapping("/products/{productId}/reviews")
    @Operation(summary = "스튜디오 상품 리뷰 작성", description = "스튜디오 상품 리뷰 작성")
    public ResponseEntity<?> createReview(
            @ModelAttribute ReviewRequest reviewRequest,
            @PathVariable("studioId") Long studioId,
            @PathVariable("productId") Long productId,
            Principal principal
    ) {
        Long memberId = PrincipalUtils.extractMemberId(principal);
        reviewCommandService.createReview(reviewRequest, memberId, studioId, productId);
        return ApiResponse.createdSuccess("리뷰 글이 성공적으로 작성되었습니다.");
    }
}
