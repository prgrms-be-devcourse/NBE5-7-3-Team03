package com.team573.gongguri.domain.grouppurchase.service

import com.team573.gongguri.domain.chat.service.ChatService
import com.team573.gongguri.domain.grouppurchase.mapper.toDto
import com.team573.gongguri.domain.grouppurchase.repository.GroupPurchaseParticipantRepository
import com.team573.gongguri.domain.grouppurchase.repository.GroupPurchaseRepository
import com.team573.gongguri.global.exception.CustomErrorCode
import com.team573.gongguri.global.exception.CustomException
import com.team573.gongguri.util.ChatRoomUtil
import com.team573.gongguri.util.GroupParticipantUtil
import com.team573.gongguri.util.GroupPurchaseUtil
import com.team573.gongguri.util.MemberUtil
import io.kotest.core.spec.DisplayName
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull

class GroupPurchaseParticipantServiceTests {
	val groupPurchaseParticipantRepository = mockk<GroupPurchaseParticipantRepository>()
	val groupPurchaseRepository = mockk<GroupPurchaseRepository>()
	val chatService = mockk<ChatService>()

	val service = GroupPurchaseParticipantService(
		groupPurchaseParticipantRepository,
		groupPurchaseRepository,
		chatService,
	)

	@Nested
	@DisplayName("cancelParticipation 는")
	inner class cancelParticipationTests {
		@Test
		fun `요청한 회원이 관리자이고 참가자가 미결제 상태면 강퇴 상태로 변환한다`() {
			// given
			val groupPurchaseId: Long = 1
			val participantId: Long = 1
			val memberId: Long = 1
			val member = MemberUtil.createWithId(memberId)
			val chatRoom = ChatRoomUtil.createWithId(1)
			val groupPurchase = GroupPurchaseUtil.createWithId(groupPurchaseId, member, chatRoom)
			val groupPurchaseParticipant = GroupParticipantUtil.createWithId(participantId, member, groupPurchase)

			every { groupPurchaseRepository.existsByGroupIdAndMember_MemberId(groupPurchaseId, memberId) } returns true
			every { groupPurchaseParticipantRepository.findByIdOrNull(participantId) } returns groupPurchaseParticipant
			every { chatService.deleteChatParticipation(groupPurchaseId, memberId) } just Runs
			every { groupPurchaseParticipantRepository.save(groupPurchaseParticipant) } returns groupPurchaseParticipant

			// when
			service.cancelParticipation(groupPurchaseId, participantId, memberId)

			// then
			verify(exactly = 1) { groupPurchaseParticipantRepository.save(groupPurchaseParticipant) }
		}

		@Test
		fun `참여자가 존재하지 않으면 예외가 발생한다`() {
			// given
			val groupPurchaseId: Long = 1
			val participantId: Long = 1
			val memberId: Long = 1

			every { groupPurchaseRepository.existsByGroupIdAndMember_MemberId(groupPurchaseId, memberId) } returns true
			every { groupPurchaseParticipantRepository.findByIdOrNull(participantId) } returns null

			// when
			val exception =
				assertThrows<CustomException> { service.cancelParticipation(groupPurchaseId, participantId, memberId) }

			// then
			exception.getCustomErrorCode() shouldBe CustomErrorCode.NOT_FOUND_PARTICIPANT
		}

		@Test
		fun `요청한 회원이 관리자가 아니면 예외가 발생한다 `() {
			// given
			val groupPurchaseId: Long = 1
			val participantId: Long = 1
			val memberId: Long = 1

			every { groupPurchaseRepository.existsByGroupIdAndMember_MemberId(groupPurchaseId, memberId) } returns false

			// when
			val exception =
				assertThrows<CustomException> { service.cancelParticipation(groupPurchaseId, participantId, memberId) }

			// then
			exception.getCustomErrorCode() shouldBe CustomErrorCode.UNAUTHORIZED_GROUP_PURCHASE_MANAGE
		}

		@Test
		fun `이미 결제한 참가자면 예외가 발생한다`() {
			// given
			val groupPurchaseId: Long = 1
			val participantId: Long = 1
			val memberId: Long = 1
			val member = MemberUtil.createWithId(memberId)
			val chatRoom = ChatRoomUtil.createWithId(1)
			val groupPurchase = GroupPurchaseUtil.createWithId(groupPurchaseId, member, chatRoom)
			val groupPurchaseParticipant = GroupParticipantUtil.createWithId(participantId, member, groupPurchase)
			groupPurchaseParticipant.confirmDeposit()

			every { groupPurchaseRepository.existsByGroupIdAndMember_MemberId(groupPurchaseId, memberId) } returns true
			every { groupPurchaseParticipantRepository.findByIdOrNull(participantId) } returns groupPurchaseParticipant

			// when
			val exception =
				assertThrows<CustomException> { service.cancelParticipation(groupPurchaseId, participantId, memberId) }

			// then
			exception.getCustomErrorCode() shouldBe CustomErrorCode.CANNOT_CANCEL_PAID_PARTICIPANT
		}
	}

	@Nested
	@DisplayName("confirmDeposit 는")
	inner class ConfirmDepositTests {
		@Test
		fun `요청한 회원이 관리자이고 참가자가 존재하면 참가자를 결제 상태로 변환한다`() {
			// given
			val groupPurchaseId: Long = 1
			val participantId: Long = 1
			val memberId: Long = 1
			val member = MemberUtil.createWithId(memberId)
			val chatRoom = ChatRoomUtil.createWithId(1)
			val groupPurchase = GroupPurchaseUtil.createWithId(groupPurchaseId, member, chatRoom)
			val groupPurchaseParticipant = GroupParticipantUtil.createWithId(participantId, member, groupPurchase)

			every { groupPurchaseRepository.existsByGroupIdAndMember_MemberId(groupPurchaseId, memberId) } returns true
			every { groupPurchaseParticipantRepository.findByIdOrNull(participantId) } returns groupPurchaseParticipant
			every { groupPurchaseParticipantRepository.save(groupPurchaseParticipant) } returns groupPurchaseParticipant

			// when
			service.confirmDeposit(groupPurchaseId, participantId, memberId)

			// then
			verify(exactly = 1) { groupPurchaseParticipantRepository.save(groupPurchaseParticipant) }
		}

		@Test
		fun `요청한 회원이 관리자가 아니면 예외를 발생한다`() {
			// given
			val groupPurchaseId: Long = 1
			val participantId: Long = 1
			val memberId: Long = 1

			every { groupPurchaseRepository.existsByGroupIdAndMember_MemberId(groupPurchaseId, memberId) } returns false

			// when
			val exception = assertThrows<CustomException> { service.confirmDeposit(groupPurchaseId, participantId, memberId) }

			// then
			exception.getCustomErrorCode() shouldBe CustomErrorCode.UNAUTHORIZED_GROUP_PURCHASE_MANAGE
		}

		@Test
		fun `참가자가 존재하지 않으면 예외를 발생한다`() {
			// given
			val groupPurchaseId: Long = 1
			val participantId: Long = 1
			val memberId: Long = 1

			every { groupPurchaseRepository.existsByGroupIdAndMember_MemberId(groupPurchaseId, memberId) } returns true
			every { groupPurchaseParticipantRepository.findByIdOrNull(participantId) } returns null

			// when
			val exception = assertThrows<CustomException> { service.confirmDeposit(groupPurchaseId, participantId, memberId) }

			// then
			exception.getCustomErrorCode() shouldBe CustomErrorCode.NOT_FOUND_PARTICIPANT
		}
	}

	@Nested
	@DisplayName("getParticipants 는")
	inner class GetParticipantsTests {
		@Test
		fun `입력한 조건에 맞는 GroupPurchaseParticipantResponseDto 리스트를 반환한다`() {
			// given
			val groupPurchaseId: Long = 1
			val memberId: Long = 1
			val size = 10
			val deposit = null
			val cursorParticipantId = null
			val pageRequest = PageRequest.of(0, size)
			val participantList = GroupParticipantUtil.createList()
			val dtoList = participantList.map { toDto(it) }

			every { groupPurchaseParticipantRepository.findParticipantsByCursor(
				groupPurchaseId,
				cursorParticipantId,
				deposit,
				memberId,
				pageRequest
			) } returns participantList

			// when
			val result = service.getParticipants(groupPurchaseId, cursorParticipantId, deposit, memberId, size)

			// then
			for (i in 0 until size) {
				result[i] shouldBe dtoList[i]
			}
		}
	}


}