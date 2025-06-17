package com.team573.gongguri.domain.grouppurchase.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.team573.gongguri.domain.grouppurchase.dto.GroupPurchaseRequestDto
import com.team573.gongguri.domain.grouppurchase.entity.ProgressStatus
import com.team573.gongguri.domain.grouppurchase.repository.GroupPurchaseRepository
import com.team573.gongguri.global.security.CustomUserDetails
import com.team573.gongguri.integration.AbstractIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.CoreMatchers.containsString
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.*

class GroupPurchaseIntegrationTests: AbstractIntegrationTest() {

	@Autowired
	lateinit var groupPurchaseRepository: GroupPurchaseRepository

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

	@Nested
	inner class CRUDtests {
		@Test
		fun `공동구매 생성 성공`() {
			val request = GroupPurchaseRequestDto(
				"공구리 공구",
				"쌉니다 싸요",
				10000,
				100,
				"여간기합은행",
				"1234567890",
				"RECRUITING",
				"image/jpeg"
			)
			val mapper = ObjectMapper()
			mockMvc.post("/api/group-purchases") {
				contentType = MediaType.APPLICATION_JSON
				content = mapper.writeValueAsString(request)
				with(user(userDetails))
			}.andExpect {
				status { isCreated() }
				jsonPath("$.title") { value("공구리 공구") }
			}.andDo {
				print()
			}
		}

		@Test
		fun `공동구매 상세 조회 성공`() {
			val groupId = 10L
			mockMvc.get("/api/group-purchases/$groupId") {
				contentType = MediaType.APPLICATION_JSON
				with(user(userDetails))
			}.andExpect {
				status { isOk() }
				jsonPath("$.title") { value("페이징 [완료글] 1") }
			}.andDo {
				print()
			}
		}

		@Test
		fun `공동구매 삭제 성공`() {
			val groupId = 10L
			mockMvc.delete("/api/group-purchases/$groupId") {
				contentType = MediaType.APPLICATION_JSON
				with(user(userDetails))
			}.andExpect {
				status { isNoContent() }
			}.andDo {
				print()
			}
		}

		@Test
		fun `공동구매 수정 성공`() {
			val groupId = 10L
			val request = GroupPurchaseRequestDto(
				"공구리 공구",
				"쌉니다 싸요",
				10000,
				100,
				"여간기합은행",
				"1234567890",
				"RECRUITING",
				"image/jpeg"
			)
			val mapper = ObjectMapper()
			mockMvc.put("/api/group-purchases/$groupId") {
				contentType = MediaType.APPLICATION_JSON
				content = mapper.writeValueAsString(request)
				with(user(userDetails))
			}.andExpect {
				status { isOk() }
			}.andDo {
				print()
			}
		}
	}

	@Nested
	inner class JoinTests {
		@Test
		fun `공동구매 참여 성공`() {
			val groupId = 10L
			val otherMember = memberRepository.findById(9).get()
			userDetails = CustomUserDetails(otherMember)
			mockMvc.post("/api/group-purchases/$groupId/join") {
				contentType = MediaType.APPLICATION_JSON
				with(user(userDetails))
			}.andExpect {
				status { isNoContent() }
			}.andDo {
				print()
			}
		}

		@Test
		fun `참여 인원 초과 시 참여 실패`() {
			val groupId = 10L
			// 남은 자리 1개 채움
			val member1 = memberRepository.findById(17).get()
			val userDetails1 = CustomUserDetails(member1)
			mockMvc.post("/api/group-purchases/$groupId/join") {
				contentType = MediaType.APPLICATION_JSON
				with(user(userDetails1))
			}.andExpect {
				status { isNoContent() }
			}

			// 초과 시도
			val member2 = memberRepository.findById(9).get()
			val userDetails2 = CustomUserDetails(member2)
			mockMvc.post("/api/group-purchases/$groupId/join") {
				contentType = MediaType.APPLICATION_JSON
				with(user(userDetails2))
			}.andExpect {
				status { isBadRequest() }
			}.andDo {
				print()
			}
		}

		@Test
		fun `이미 참여 중인 경우`() {
			val groupId = 10L
			val member = memberRepository.findById(1).get()
			val userDetails = CustomUserDetails(member)

			mockMvc.post("/api/group-purchases/$groupId/join") {
				contentType = MediaType.APPLICATION_JSON
				with(user(userDetails))
			}.andExpect {
				status { isConflict() }
				content { string(containsString("이미 공동구매에 참여하였습니다.")) }
			}.andDo {
				print()
			}
		}

		@Test
		fun `참여 정원이 가득 찬 경우 Closed로 변경`() {
			val groupId = 10L
			val member = memberRepository.findById(9).get()
			val userDetails = CustomUserDetails(member)

			mockMvc.post("/api/group-purchases/$groupId/join") {
				contentType = MediaType.APPLICATION_JSON
				with(user(userDetails))
			}.andExpect {
				status { isNoContent() }
			}
			val closedGroupPurchase = groupPurchaseRepository.findById(groupId).get()

			assertThat(closedGroupPurchase.progressStatus).isEqualTo(ProgressStatus.CLOSED)
		}
	}
}