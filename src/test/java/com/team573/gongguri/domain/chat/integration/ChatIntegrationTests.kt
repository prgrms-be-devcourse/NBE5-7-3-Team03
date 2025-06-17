package com.team573.gongguri.domain.chat.integration

import com.team573.gongguri.integration.AbstractIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.get

class ChatIntegrationTests: AbstractIntegrationTest() {

	@Test
	fun `채팅 메시지 조회 테스트`() {
		val roomId: Long = 3
		val size = 10

		mockMvc.get("/chat/$roomId/messages") {
			contentType = MediaType.APPLICATION_JSON
			param("size", size.toString())
			with(user(userDetails))
		}.andExpect {
			status { isOk() }
		}.andDo {
			print()
		}
	}
}