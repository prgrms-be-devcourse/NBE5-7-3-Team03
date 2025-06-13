package com.team573.gongguri.domain.grouppurchase.controller

import com.team573.gongguri.domain.grouppurchase.entity.PurchaseFilter
import com.team573.gongguri.domain.grouppurchase.service.GroupPurchaseParticipantService
import com.team573.gongguri.domain.grouppurchase.service.GroupPurchaseService
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
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch

@WebMvcTest(GroupPurchaseController::class)
class GroupPurchaseControllerTests {
	@Autowired
	lateinit var mockMvc: MockMvc

	@MockitoBean
	lateinit var groupPurchaseService: GroupPurchaseService

	@MockitoBean
	lateinit var groupPurchaseParticipantService: GroupPurchaseParticipantService

	@Test
	fun `getWithChat는 채팅 메시지와 공동구매를 조회한다`() {
		// given
		val cursorGroupPurchaseId = 1L
		val purchaseFilter = PurchaseFilter.ALL
		val size = 10
		val memberId: Long = 1

		`when`(groupPurchaseService.getWithMessage(
			size,
			cursorGroupPurchaseId,
			purchaseFilter,
			memberId
		)).thenReturn(listOf())

		// when & then
		mockMvc.get("/api/group-purchases/chat") {
			contentType = MediaType.APPLICATION_JSON
			param("cursor", cursorGroupPurchaseId.toString())
			param("progressStatus", "ALL")
			param("size", size.toString())
			with(user(AuthUtil.createUserDetails(MemberUtil.createWithId(memberId))))
		}.andExpect {
			status { isOk() }
			jsonPath("$.size()") { value(0) }
		}
	}

	@Test
	fun `confirmDeposit는 결제 상태를 변경한다`() {
		// given
		val groupPurchaseId: Long = 1
		val participantsId: Long = 1
		val memberId: Long = 1

		// when & then
		mockMvc.patch("/api/group-purchases/$groupPurchaseId/participants/$participantsId/confirm") {
			contentType = MediaType.APPLICATION_JSON
			with(user(AuthUtil.createUserDetails(MemberUtil.createWithId(memberId))))
			with(csrf())
		}.andExpect {
			status { isNoContent() }
		}
	}

	@Test
	fun `cancelParticipantStatus는 참가자를 강퇴한다`() {
		// given
		val groupPurchaseId: Long = 1
		val participantsId: Long = 1
		val memberId: Long = 1

		// when & then
		mockMvc.patch("/api/group-purchases/$groupPurchaseId/participants/$participantsId/cancel") {
			contentType = MediaType.APPLICATION_JSON
			with(user(AuthUtil.createUserDetails(MemberUtil.createWithId(memberId))))
			with(csrf())
		}.andExpect {
			status { isNoContent() }
		}
	}

	@Test
	fun `getParticipants는 참가자 목록을 조회한다`() {
		// given
		val groupPurchaseId: Long = 1
		val cursor = null
		val deposit = null
		val size = 10
		val memberId: Long = 1

		`when` (groupPurchaseParticipantService.getParticipants(groupPurchaseId, cursor, deposit, memberId, size)).thenReturn(listOf())

		// when & then
		mockMvc.get("/api/group-purchases/$groupPurchaseId/participants") {
			contentType = MediaType.APPLICATION_JSON
			with(user(AuthUtil.createUserDetails(MemberUtil.createWithId(memberId))))
		}.andExpect {
			status { isOk() }
			jsonPath("$.size()") { value(0) }
		}
	}

	@Test
	fun `getAllByCursor는 공동 구매를 조회한다`() {
		val cursorGroupPurchaseId = 1L
		val purchaseFilter = PurchaseFilter.ALL
		val size = 10
		val memberId: Long = 1
		val progressStatuses = purchaseFilter.toStatuses()

		`when` (groupPurchaseService.getAllByCursor(
			cursorGroupPurchaseId,
			progressStatuses,
			size
		)).thenReturn(listOf())

		// when & then
		mockMvc.get("/api/group-purchases") {
			contentType = MediaType.APPLICATION_JSON
			with(user(AuthUtil.createUserDetails(MemberUtil.createWithId(memberId))))
		}.andExpect {
			status { isOk() }
			jsonPath("$.size()") { value(0) }
		}
	}
}