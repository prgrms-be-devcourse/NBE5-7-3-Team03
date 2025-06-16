package com.team573.gongguri.domain.review.controller

import com.team573.gongguri.domain.review.service.ReviewService
import com.team573.gongguri.util.AuthUtil
import com.team573.gongguri.util.MemberUtil
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@WebMvcTest(controllers = [ReviewController::class])
class ReviewControllerTests {
	@Autowired
	lateinit var mockMvc: MockMvc

	@MockitoBean
	lateinit var reviewService: ReviewService

	@Test
	fun `review는 리뷰를 저장하고 리뷰의 ID를 반환한다`() {
		// given
		val groupPurchaseId = 1L
		val like = true
		val memberId = 1L
		val reviewId = 1L

		`when` (reviewService.addReview(groupPurchaseId, memberId, like)).thenReturn(reviewId)

		// when & then
		mockMvc.post("/api/group-purchases/$groupPurchaseId/review") {
			contentType = MediaType.APPLICATION_JSON
			param("like", "true")
			with(user(AuthUtil.createUserDetails(MemberUtil.createWithId(1))))
			with(csrf())
		}.andExpect {
			status { isOk() }
			jsonPath("$") { value(reviewId) }
		}

	}
}