package com.team573.gongguri.domain.grouppurchase.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.team573.gongguri.global.security.CustomUserDetails
import com.team573.gongguri.integration.AbstractIntegrationTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch

class GroupPurchaseIntegrationTests: AbstractIntegrationTest() {

	@Nested
	inner class GetWithChatTests {
		@Test
		fun `채팅방 리스트 조회`() {
			val size = 10

			val firstResult = mockMvc.get("/api/group-purchases/chat") {
				contentType = MediaType.APPLICATION_JSON
				param("progressStatus", "ALL")
				param("size", size.toString())
				with(user(userDetails))
			}.andExpect {
				status { isOk() }
			}.andDo {
				print()
			}.andReturn()

			val content = firstResult.response.contentAsString
			val objectMapper = ObjectMapper()
			val rootNode = objectMapper.readTree(content)
			val isEmpty = rootNode.isEmpty

			if (!isEmpty) {
				val cursor = rootNode.last().get("id").asLong()
				mockMvc.get("/api/group-purchases/chat") {
					contentType = MediaType.APPLICATION_JSON
					param("progressStatus", "ALL")
					param("cursor", cursor.toString())
					param("size", size.toString())
					with(user(userDetails))
				}.andExpect {
					status { isOk() }
				}.andDo {
					print()
				}
			}
		}
	}

	@Nested
	inner class ConfirmDepositTests {
		@Test
		fun `결제 상태로 변환`() {
			val groupPurchaseId: Long = 10
			val participantsId: Long = 12

			mockMvc.patch("/api/group-purchases/$groupPurchaseId/participants/$participantsId/confirm") {
				contentType = MediaType.APPLICATION_JSON
				with(user(userDetails))
				with(csrf())
			}.andExpect {
				status { isNoContent() }
			}.andDo {
				print()
			}
		}

		@Test
		fun `관리자가 아닐 경우 결제 상태로 변환 예외`() {
			val groupPurchaseId: Long = 10
			val participantsId: Long = 12

			val member2 = memberRepository.findById(9).get()

			mockMvc.patch("/api/group-purchases/$groupPurchaseId/participants/$participantsId/confirm") {
				contentType = MediaType.APPLICATION_JSON
				with(user(CustomUserDetails(member2)))
				with(csrf())
			}.andExpect {
				status { isForbidden() }
			}.andDo {
				print()
			}
		}
	}

	@Nested
	inner class CancelParticipantStatusTests {
		@Test
		fun `참가자 강퇴`() {
			val groupPurchaseId: Long = 10
			val participantsId: Long = 12

			mockMvc.patch("/api/group-purchases/$groupPurchaseId/participants/$participantsId/cancel") {
				contentType = MediaType.APPLICATION_JSON
				with(user(userDetails))
				with(csrf())
			}.andExpect {
				status { isNoContent() }
			}.andDo {
				print()
			}
		}

		@Test
		fun `참가자가 이미 결제를 했을 경우 참가자 강퇴 예외`() {
			val groupPurchaseId: Long = 10
			val participantsId: Long = 12

			mockMvc.patch("/api/group-purchases/$groupPurchaseId/participants/$participantsId/confirm") {
				contentType = MediaType.APPLICATION_JSON
				with(user(userDetails))
				with(csrf())
			}.andExpect {
				status { isNoContent() }
			}.andDo {
				print()
			}

			mockMvc.patch("/api/group-purchases/$groupPurchaseId/participants/$participantsId/cancel") {
				contentType = MediaType.APPLICATION_JSON
				with(user(userDetails))
				with(csrf())
			}.andExpect {
				status { isForbidden() }
			}.andDo {
				print()
			}
		}
	}

	@Nested
	inner class GetParticipantsTests {
		@Test
		fun `참가자 목록을 조회`() {
			val groupPurchaseId: Long = 10
			val size = 10

			val firstResult = mockMvc.get("/api/group-purchases/$groupPurchaseId/participants") {
				contentType = MediaType.APPLICATION_JSON
				param("size", size.toString())
				with(user(userDetails))
			}.andExpect {
				status { isOk() }
			}.andDo {
				print()
			}.andReturn()

			val content = firstResult.response.contentAsString
			val objectMapper = ObjectMapper()
			val rootNode = objectMapper.readTree(content)
			val isEmpty = rootNode.isEmpty

			if (!isEmpty) {
				val cursor = rootNode.last().get("groupParticipantId").asLong()
				mockMvc.get("/api/group-purchases/$groupPurchaseId/participants") {
					contentType = MediaType.APPLICATION_JSON
					param("size", size.toString())
					param("cursor", cursor.toString())
					with(user(userDetails))
				}.andExpect {
					status { isOk() }
				}.andDo {
					print()
				}
			}
		}

	}

	@Nested
	inner class GetAllByCursorTests {
		@Test
		fun `공동 구매를 조회`() {
			val size = 10

			val firstResult = mockMvc.get("/api/group-purchases") {
				contentType = MediaType.APPLICATION_JSON
				param("size", size.toString())
				with(user(userDetails))
			}.andExpect {
				status { isOk() }
			}.andDo {
				print()
			}.andReturn()

			val content = firstResult.response.contentAsString
			val objectMapper = ObjectMapper()
			val rootNode = objectMapper.readTree(content)
			val isEmpty = rootNode.isEmpty

			if (!isEmpty) {
				val cursor = rootNode.last().get("id").asLong()
				mockMvc.get("/api/group-purchases") {
					contentType = MediaType.APPLICATION_JSON
					param("cursor", cursor.toString())
					param("size", size.toString())
					with(user(userDetails))
				}.andExpect {
					status { isOk() }
				}.andDo {
					print()
				}
			}
		}
	}
}