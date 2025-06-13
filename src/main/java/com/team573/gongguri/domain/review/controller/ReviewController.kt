package com.team573.gongguri.domain.review.controller

import com.team573.gongguri.domain.review.service.ReviewService
import com.team573.gongguri.global.security.CustomUserDetails
import lombok.RequiredArgsConstructor
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/group-purchases")
@RequiredArgsConstructor
class ReviewController(
	private val reviewService: ReviewService
) {

	@PostMapping("/{groupPurchaseId}/review")
	fun review(
		@PathVariable("groupPurchaseId") groupPurchaseId: Long,
		@RequestParam like: Boolean,
		@AuthenticationPrincipal customUserDetails: CustomUserDetails
	): ResponseEntity<Long> {
		val reviewId = reviewService.addReview(groupPurchaseId, customUserDetails.memberId, like)
		return ResponseEntity.ok(reviewId)
	}
}
