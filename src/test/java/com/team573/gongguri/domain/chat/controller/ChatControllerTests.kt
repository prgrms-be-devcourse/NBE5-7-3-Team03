package com.team573.gongguri.domain.chat.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.team573.gongguri.domain.chat.entity.ChatMessage
import com.team573.gongguri.domain.chat.mapper.toDto
import com.team573.gongguri.domain.chat.service.ChatService
import com.team573.gongguri.util.AuthUtil
import com.team573.gongguri.util.ChatMessageUtil
import com.team573.gongguri.util.MemberUtil
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@WebMvcTest(ChatController::class)
class ChatControllerTests {
	@Autowired
	lateinit var mockMvc: MockMvc

	@MockitoBean
	lateinit var chatService: ChatService

	@Autowired
	lateinit var om: ObjectMapper

	@Test
	fun `getMessages는 조건에 해당하는 메시지 목록을 반환한다`() {
		// given
		val roomId: Long = 1
		val cursor = ObjectId().toHexString()
		val size = 10

		val messages = ChatMessageUtil.createList().stream()
			.map { chatMessage: ChatMessage -> toDto(chatMessage) }
			.toList()

		// when
		`when`(chatService.getMessages(roomId, cursor, size)).thenReturn(messages)

		// then
		mockMvc.get("/chat/$roomId/messages") {
			contentType = MediaType.APPLICATION_JSON
			param("cursor", cursor)
			param("size", size.toString())
			with(user(AuthUtil.createUserDetails(MemberUtil.createWithId(1))))
		}.andExpect {
			status { isOk() }
			jsonPath("$.size()") { value(messages.size.toLong()) }
		}
	}
}