package com.team573.gongguri.domain.grouppurchase.service

import com.team573.gongguri.domain.chat.service.ChatService
import com.team573.gongguri.domain.grouppurchase.entity.ParticipationStatus
import com.team573.gongguri.domain.grouppurchase.entity.PurchaseFilter
import com.team573.gongguri.domain.grouppurchase.mapper.toListDto
import com.team573.gongguri.domain.grouppurchase.repository.GroupPurchaseJpqlRepository
import com.team573.gongguri.domain.grouppurchase.repository.GroupPurchaseParticipantRepository
import com.team573.gongguri.domain.grouppurchase.repository.GroupPurchaseRepository
import com.team573.gongguri.domain.member.repository.MemberRepository
import com.team573.gongguri.domain.member.service.MemberService
import com.team573.gongguri.global.exception.CustomErrorCode
import com.team573.gongguri.global.exception.CustomException
import com.team573.gongguri.util.ChatRoomUtil
import com.team573.gongguri.util.GroupParticipantUtil
import com.team573.gongguri.util.GroupPurchaseUtil
import com.team573.gongguri.util.MemberUtil
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull

class GroupPurchaseServiceTests {
	val groupPurchaseRepository = mockk<GroupPurchaseRepository>()
	val memberRepository = mockk<MemberRepository>()
	val chatService = mockk<ChatService>()
	val groupPurchaseJpqlRepository = mockk<GroupPurchaseJpqlRepository>()
	val groupPurchaseParticipantRepository = mockk<GroupPurchaseParticipantRepository>()
	val memberService = mockk<MemberService>()

	val service = GroupPurchaseService(
		groupPurchaseRepository,
		memberRepository,
		chatService,
		groupPurchaseJpqlRepository,
		groupPurchaseParticipantRepository,
		memberService
	)

	@Nested
	inner class GetWithMessageTests {
        @Test
        fun `조건에 해당하는 GroupPurchaseWithChatResponseDto 리스트를 반환한다`() {
			// given
			val size = 10
	        val cursorId = null
	        val purchaseFilter = PurchaseFilter.ALL
	        val memberId: Long = 1
	        val statuses = purchaseFilter.toStatuses()
	        val pageable = PageRequest.of(0, size)
	        val member = MemberUtil.createWithId(memberId)
	        val participantList = GroupParticipantUtil.createList(member)
	        val groupPurchases = participantList.map { it.groupPurchase }
	        val chatRoomIdList = groupPurchases.map { it.chatRoom.chatRoomId!! }
	        val firstMessageMap = mutableMapOf<Long, String>()

	        for (i in 1L..10L) {
		        firstMessageMap[i] = "test"
	        }

	        every { groupPurchaseParticipantRepository.findByMemberWithCursor(cursorId, memberId, statuses, pageable) } returns participantList
	        every { groupPurchaseParticipantRepository.countByGroupPurchaseAndParticipationStatus(any(), any()) } returns 100
	        every { chatService.getFirstMessageMap(chatRoomIdList) } returns firstMessageMap

	        // when
	        val result = service.getWithMessage(size, cursorId, purchaseFilter, memberId)

	        // then
	        for(i in 0 until size) {
				result[i].participantId shouldBe participantList[i].groupParticipantId
			}
        }
	}

	@Nested
	inner class GetSimpleInfoTests {
		@Test
		fun `공동 구매의 간단한 정보를 반환한다`() {
			// given
			val groupPurchaseId: Long = 1
			val member =  MemberUtil.createWithId(1)
			val chatRoom = ChatRoomUtil.createWithId(1)
			val groupPurchase = GroupPurchaseUtil.createWithId(groupPurchaseId, member, chatRoom)
			val participantCount: Long = 10

			every { groupPurchaseRepository.findByIdOrNull(groupPurchaseId) } returns groupPurchase
			every { groupPurchaseParticipantRepository.countByGroupPurchaseAndParticipationStatus(groupPurchase, ParticipationStatus.JOINED) } returns participantCount

			// when
			val result = service.getSimpleInfo(groupPurchaseId)

			// then
			result.id shouldBe groupPurchaseId
			result.participantCount shouldBe participantCount
		}

		@Test
		fun `공동 구매가 존재하지 않으면 예외가 발생한다`() {
			// given
			val groupPurchaseId: Long = 1

			every { groupPurchaseRepository.findByIdOrNull(groupPurchaseId) } returns null

			// when
			val exception = assertThrows<CustomException>{ service.getSimpleInfo(groupPurchaseId) }

			// then
			exception.getCustomErrorCode() shouldBe CustomErrorCode.NOT_FOUND_GROUP_PURCHASE
		}
	}

	@Nested
	inner class FindCreatedPurchases {
		@Test
		fun `purchaseFilter가 ALL이면 조건에 해당하는 공동구매글 전체를 조회한다`() {
			// given
			val memberId: Long = 1
			val purchaseFilter = PurchaseFilter.ALL
			val participantCount: Long = 10
			val member = MemberUtil.createWithId(memberId)
			val purchaseList = GroupPurchaseUtil.createList(member)

			every { memberRepository.findByIdOrNull(memberId) } returns member
			every { groupPurchaseRepository.findByMember_MemberId(memberId) } returns purchaseList.toList()
			every { groupPurchaseParticipantRepository.countByGroupPurchaseAndParticipationStatus(any(), ParticipationStatus.JOINED) } returns participantCount

			val dtoList = purchaseList.map { toListDto(it, participantCount) }

			// when
			val result = service.findCreatedPurchases(memberId, purchaseFilter)

			// then
			result shouldBe dtoList
		}

		@Test
		fun `purchaseFilter가 ALL이 아니면 조건에 해당하는 공동구매글을 조회한다`() {
			// given
			val memberId: Long = 1
			val purchaseFilter = PurchaseFilter.ONGOING
			val participantCount: Long = 10
			val member = MemberUtil.createWithId(memberId)
			val purchaseList = GroupPurchaseUtil.createList(member)

			every { memberRepository.findByIdOrNull(memberId) } returns member
			every { groupPurchaseRepository.findByMember_MemberIdAndProgressStatusIn(memberId, purchaseFilter.toStatuses()) } returns purchaseList.toList()
			every { groupPurchaseParticipantRepository.countByGroupPurchaseAndParticipationStatus(any(), ParticipationStatus.JOINED) } returns participantCount

			val dtoList = purchaseList.map { toListDto(it, participantCount) }

			// when
			val result = service.findCreatedPurchases(memberId, purchaseFilter)

			// then
			result shouldBe dtoList
		}

		@Test
		fun `회원이 존재하지 않으면 예외를 발생한다`() {
			// given
			val memberId: Long = 1
			val purchaseFilter = PurchaseFilter.ONGOING

			every { memberRepository.findByIdOrNull(memberId) } returns null

			// when
			val exception = assertThrows<CustomException> { service.findCreatedPurchases(memberId, purchaseFilter) }

			// then
			exception.getCustomErrorCode() shouldBe CustomErrorCode.NOT_FOUND_MEMBER
		}
	}
}