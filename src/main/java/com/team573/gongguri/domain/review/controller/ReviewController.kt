package com.team573.gongguri.domain.review.controller;

import com.team573.gongguri.domain.review.service.ReviewService;
import com.team573.gongguri.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/group-purchases")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/{groupPurchaseId}/review")
    public ResponseEntity<Long> review(
        @PathVariable("groupPurchaseId") Long groupPurchaseId,
        @RequestParam Boolean like,
        @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        Long reviewId = reviewService.addReview(groupPurchaseId, customUserDetails.getMemberId(), like);
        return ResponseEntity.ok(reviewId);
    }
}
