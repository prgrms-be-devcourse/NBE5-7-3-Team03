package com.team573.gongguri.domain.review.controller

import com.team573.gongguri.domain.grouppurchase.service.GroupPurchaseService
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
@RequiredArgsConstructor
class ReviewViewController(
	private val groupPurchaseService: GroupPurchaseService
) {

	@GetMapping("/group-purchase/{groupPurchaseId}/review")
	fun review(
		@PathVariable groupPurchaseId: Long,
		model: Model
	): String {
		val simpleInfo = groupPurchaseService.getSimpleInfo(groupPurchaseId)
		model.addAttribute("simpleInfo", simpleInfo)
		return "review/review"
	}
}
