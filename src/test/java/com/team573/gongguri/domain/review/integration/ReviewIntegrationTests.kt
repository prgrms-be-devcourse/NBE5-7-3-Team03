package com.team573.gongguri.domain.review.integration

import com.team573.gongguri.global.security.CustomUserDetails
import com.team573.gongguri.integration.AbstractIntegrationTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.post

class ReviewIntegrationTests: AbstractIntegrationTest() {
	@Nested
	inner class ReviewTests {
		@Test
		fun `리뷰를 저장한다`() {
			val groupPurchaseId = 30

			val member9 = memberRepository.findById(9).get()

			mockMvc.post("/api/group-purchases/$groupPurchaseId/review") {
				contentType = MediaType.APPLICATION_JSON
				param("like", "true")
				with(user(CustomUserDetails(member9)))
				with(csrf())
			}.andExpect {
				status { isOk() }
			}.andDo {
				print()
			}
		}

		@Test
		fun `완료된 공동 구매가 아니면 예외가 발생한다`() {
			val groupPurchaseId = 27

			mockMvc.post("/api/group-purchases/$groupPurchaseId/review") {
				contentType = MediaType.APPLICATION_JSON
				param("like", "true")
				with(user(userDetails))
				with(csrf())
			}.andExpect {
				status { isBadRequest() }
			}.andDo {
				print()
			}
		}
	}
}