package com.team573.gongguri.domain.grouppurchase.service

import com.team573.gongguri.domain.chat.service.ChatService
import com.team573.gongguri.domain.grouppurchase.dto.GroupPurchaseRequestDto
import com.team573.gongguri.domain.grouppurchase.dto.GroupPurchaseWithParticipantCountDto
import com.team573.gongguri.domain.grouppurchase.entity.ParticipationStatus
import com.team573.gongguri.domain.grouppurchase.entity.ProgressStatus
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
import io.mockk.*
import org.junit.jupiter.api.*
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime

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

    @Nested
    inner class CRUDtests {
        @Test
        @DisplayName("공동구매 생성 성공 테스트")
        fun addGroupPurchaseTests_successfully() {
            val groupPurchaseId: Long = 1
            val memberId: Long = 1
            val member = MemberUtil.createWithId(1)
            val chatRoom = ChatRoomUtil.createWithId(1)
            val groupPurchase = GroupPurchaseUtil.createWithId(groupPurchaseId, member, chatRoom)

            val groupPurchaseRequestDto = GroupPurchaseRequestDto(
            "공구리 공구",
            "쌉니다 싸요",
            10000,
            100,
            "여간기합은행",
            "1234567890",
            "RECRUITING",
            "image/jpeg"
             )

            //given
            every { memberService.getMemberById(memberId) } returns member
            every { chatService.addChatRoom(member.email) } returns chatRoom
            every { groupPurchaseRepository.save(any()) } returns groupPurchase
            every { groupPurchaseParticipantRepository.save(any()) } returns mockk()

            //when
            val result = service.add(groupPurchaseRequestDto, memberId)

            // then
            result.title shouldBe "공구리 공구"
            result.id shouldBe 1L
            result.progressStatus shouldBe "RECRUITING"

            verify(exactly = 1) { memberService.getMemberById(memberId) }
            verify(exactly = 1) { chatService.addChatRoom(member.email) }
            verify(exactly = 1) { groupPurchaseRepository.save(any()) }
            verify(exactly = 1) { groupPurchaseParticipantRepository.save(any()) }
        }



        @Test
        @DisplayName("공동구매 상세 조회 성공 테스트")
        fun getGroupPurchaseTests_successfully() {
            val groupPurchaseId: Long = 1
            val memberId: Long = 1
            val member = MemberUtil.createWithId(1)
            val chatRoom = ChatRoomUtil.createWithId(1)
            val groupPurchase = GroupPurchaseUtil.createWithId(groupPurchaseId, member, chatRoom)
            // given
            every { groupPurchaseRepository.findByIdOrNull(groupPurchaseId) } returns groupPurchase
            every { groupPurchaseParticipantRepository.countByGroupPurchaseAndParticipationStatus(groupPurchase, ParticipationStatus.JOINED) } returns 2L
            every { groupPurchaseParticipantRepository.existsByGroupPurchase_GroupIdAndMember_MemberId(groupPurchaseId, memberId) } returns true

            // when
            val result = service.get(groupPurchaseId, memberId)

            // then
            result.title shouldBe "공구리 공구"
            result.isParticipated shouldBe true
            result.currentParticipants shouldBe 2L

            verify(exactly = 1) { groupPurchaseRepository.findByIdOrNull(groupPurchaseId) }
            verify(exactly = 1) { groupPurchaseParticipantRepository.countByGroupPurchaseAndParticipationStatus(groupPurchase, ParticipationStatus.JOINED) }
            verify(exactly = 1) { groupPurchaseParticipantRepository.existsByGroupPurchase_GroupIdAndMember_MemberId(groupPurchaseId, memberId) }
        }


        @Test
        @DisplayName("공동구매 수정 성공 테스트")
        fun updateGroupPurchaseTests_successfully() {
            val groupPurchaseId: Long = 1
            val member = MemberUtil.createWithId(1)
            val chatRoom = ChatRoomUtil.createWithId(1)
            val groupPurchase = GroupPurchaseUtil.createWithId(groupPurchaseId, member, chatRoom)
            // given
            val updateDto = GroupPurchaseRequestDto(
                "수정된 제목",
                "수정된 내용",
                12000,
                6,
                "신한은행",
                "333-3333-3333-33",
                "CLOSED",
                "https://newimage.com/item.jpg"
            )
            every { groupPurchaseRepository.findByGroupIdAndDeletedFalse(groupPurchaseId) } returns groupPurchase

            // when
            val result = service.update(groupPurchaseId, updateDto)

            // then
            result.title shouldBe "수정된 제목"
            result.progressStatus shouldBe "CLOSED"
            result.imageUrl shouldBe "https://newimage.com/item.jpg"

            verify(exactly = 1) { groupPurchaseRepository.findByGroupIdAndDeletedFalse(groupPurchaseId) }
        }

        @Test
        @DisplayName("공동구매 삭제 성공 테스트")
        fun deleteGroupPurchaseTests_successfully() {
            val groupPurchaseId: Long = 1
            val memberId: Long = 1
            val member = MemberUtil.createWithId(1)
            val chatRoom = ChatRoomUtil.createWithId(1)
            val groupPurchase = GroupPurchaseUtil.createWithId(groupPurchaseId, member, chatRoom)
            // given
            every { groupPurchaseRepository.findByGroupIdAndDeletedFalse(groupPurchaseId) } returns groupPurchase
            every { groupPurchaseParticipantRepository.existsByGroupPurchase_GroupIdAndDepositIsTrue(groupPurchaseId) } returns false

            // when
            service.delete(groupPurchaseId, memberId)

            // then
            groupPurchase.deleted shouldBe true

            verify(exactly = 1) { groupPurchaseRepository.findByGroupIdAndDeletedFalse(groupPurchaseId) }
            verify(exactly = 1) { groupPurchaseParticipantRepository.existsByGroupPurchase_GroupIdAndDepositIsTrue(groupPurchaseId) }
        }

    }

    @Nested
    inner class JoinTests {
        @Test
        @DisplayName("공동구매 참여 성공 테스트")
        fun joinGroupPurchaseTests_successfully() {
            val groupPurchaseId: Long = 1
            val memberId: Long = 1
            val member = MemberUtil.createWithId(1)
            val chatRoom = ChatRoomUtil.createWithId(1)
            val groupPurchase = GroupPurchaseUtil.createWithId(groupPurchaseId, member, chatRoom)
            // given
            every { memberService.getMemberById(memberId) } returns member
            every { groupPurchaseParticipantRepository.existsByGroupPurchase_GroupIdAndMember_MemberId(groupPurchaseId, memberId) } returns false
            every { groupPurchaseRepository.findByGroupIdAndDeletedFalse(groupPurchaseId) } returns groupPurchase
            every { groupPurchaseParticipantRepository.countByGroupPurchaseAndParticipationStatus(groupPurchase, ParticipationStatus.JOINED) } returns 1L
            every { chatService.addChatParticipation(groupPurchase.chatRoom.chatRoomId!!, member.email) } just Runs
            every { groupPurchaseParticipantRepository.save(any()) } returns mockk()

            // when
            service.join(groupPurchaseId, memberId)

            // then
            groupPurchase.progressStatus shouldBe ProgressStatus.RECRUITING

            verify(exactly = 1) { memberService.getMemberById(memberId) }
            verify(exactly = 1) { groupPurchaseParticipantRepository.existsByGroupPurchase_GroupIdAndMember_MemberId(groupPurchaseId, memberId) }
            verify(exactly = 1) { chatService.addChatParticipation(groupPurchase.chatRoom.chatRoomId!!, member.email) }
            verify(exactly = 1) { groupPurchaseParticipantRepository.save(any()) }
            verify(exactly = 1) { groupPurchaseParticipantRepository.countByGroupPurchaseAndParticipationStatus(groupPurchase, ParticipationStatus.JOINED) }
            verify(exactly = 1) { groupPurchaseRepository.findByGroupIdAndDeletedFalse(groupPurchaseId) }
        }

        @Test
        @DisplayName("이미 공동구매에 참여중인 경우 예외 테스트")
        fun joinGroupPurchaseTests_alreadyJoined_theowsException() {
            val groupPurchaseId: Long = 1
            val memberId: Long = 1
            val member = MemberUtil.createWithId(1)
            val chatRoom = ChatRoomUtil.createWithId(1)
            val groupPurchase = GroupPurchaseUtil.createWithId(groupPurchaseId, member, chatRoom)
            // given
            every { memberService.getMemberById(memberId) } returns member
            every { groupPurchaseParticipantRepository.existsByGroupPurchase_GroupIdAndMember_MemberId(groupPurchaseId, memberId) } returns true
            every { groupPurchaseRepository.findByGroupIdAndDeletedFalse(groupPurchaseId) } returns groupPurchase
            every { groupPurchaseParticipantRepository.countByGroupPurchaseAndParticipationStatus(groupPurchase, ParticipationStatus.JOINED) } returns 1L

            // when & then
            val exception = assertThrows<CustomException> { service.join(groupPurchaseId, memberId) }
            exception.getCustomErrorCode() shouldBe CustomErrorCode.ALREADY_JOINED
            verify(exactly = 1) { memberService.getMemberById(memberId) }
            verify(exactly = 1) { groupPurchaseRepository.findByGroupIdAndDeletedFalse(groupPurchaseId) }
            verify(exactly = 1) { groupPurchaseParticipantRepository.existsByGroupPurchase_GroupIdAndMember_MemberId(groupPurchaseId, memberId) }
            verify(exactly = 1) { groupPurchaseParticipantRepository.countByGroupPurchaseAndParticipationStatus(groupPurchase, ParticipationStatus.JOINED)  }
        }

        @Test
        @DisplayName("공동구매 참여 인원 초과 예외 테스트")
        fun joinGroupPurchaseTests_participantLimitExceeded_theowsException() {
            val groupPurchaseId: Long = 1
            val memberId: Long = 1
            val member = MemberUtil.createWithId(1)
            val chatRoom = ChatRoomUtil.createWithId(1)
            val groupPurchase = GroupPurchaseUtil.createWithId(groupPurchaseId, member, chatRoom)
            // given
            every { memberService.getMemberById(memberId) } returns member
            every { groupPurchaseRepository.findByGroupIdAndDeletedFalse(groupPurchaseId) } returns groupPurchase
            every { groupPurchaseParticipantRepository.countByGroupPurchaseAndParticipationStatus(groupPurchase, ParticipationStatus.JOINED) } returns groupPurchase.maxParticipants.toLong()

            // when & then
            val exception = assertThrows<CustomException> { service.join(groupPurchaseId, memberId) }
            exception.getCustomErrorCode() shouldBe CustomErrorCode.PARTICIPANT_LIMIT_REACHED
            verify(exactly = 1) { memberService.getMemberById(memberId) }
            verify(exactly = 1) { groupPurchaseRepository.findByGroupIdAndDeletedFalse(groupPurchaseId) }
            verify(exactly = 1) { groupPurchaseParticipantRepository.countByGroupPurchaseAndParticipationStatus(groupPurchase, ParticipationStatus.JOINED) }
        }


        @Test
        @DisplayName("참여 후 정원이 가득 차면 상태가 CLOSED로 변경 테스트")
        fun joinGroupPurchaseTests_reachesMaxParticipants_theowsException() {
            val groupPurchaseId: Long = 1
            val memberId: Long = 1
            val member = MemberUtil.createWithId(1)
            val chatRoom = ChatRoomUtil.createWithId(1)
            val groupPurchase = GroupPurchaseUtil.createWithId(groupPurchaseId, member, chatRoom)
            // given
            every { memberService.getMemberById(memberId) } returns member
            every { groupPurchaseRepository.findByGroupIdAndDeletedFalse(groupPurchaseId) } returns groupPurchase
            every { groupPurchaseParticipantRepository.countByGroupPurchaseAndParticipationStatus(groupPurchase, ParticipationStatus.JOINED) } returns (groupPurchase.maxParticipants - 1).toLong()
            every { groupPurchaseParticipantRepository.existsByGroupPurchase_GroupIdAndMember_MemberId(groupPurchaseId, memberId) } returns false
            every { chatService.addChatParticipation(any(), any()) } returns Unit
            every { groupPurchaseParticipantRepository.save(any()) } returns mockk()

            // when
            service.join(groupPurchaseId, memberId)

            // then
            groupPurchase.progressStatus shouldBe ProgressStatus.CLOSED
            verify(exactly = 1) { memberService.getMemberById(memberId) }
            verify(exactly = 1) { groupPurchaseRepository.findByGroupIdAndDeletedFalse(groupPurchaseId) }
            verify(exactly = 1) { groupPurchaseParticipantRepository.countByGroupPurchaseAndParticipationStatus(groupPurchase, ParticipationStatus.JOINED) }
            verify(exactly = 1) { groupPurchaseParticipantRepository.existsByGroupPurchase_GroupIdAndMember_MemberId(groupPurchaseId, memberId) }
            verify(exactly = 1) { groupPurchaseParticipantRepository.save(any()) }
            verify(exactly = 1) { chatService.addChatParticipation(any(), any()) }
        }

    }

    @Nested
    inner class GetAllByCursor {
        @Test
        @DisplayName("커서 기반 공동구매 조회 성공 테스트")
        fun allGroupPurchaseTests_successfully() {
            // given
            val cursorId: Long? = null
            val progressStatuses = listOf(ProgressStatus.RECRUITING)
            val size = 3
            val dto1 = GroupPurchaseWithParticipantCountDto(
                101L,
                "제목1",
                "내용1",
                10000,
                5,
                ProgressStatus.RECRUITING,
                LocalDateTime.now(),
                null,
                1L,
                "url1"
            )

            val dto2 = GroupPurchaseWithParticipantCountDto(
                102L,
                "제목2",
                "내용2",
                20000,
                3,
                ProgressStatus.RECRUITING,
                LocalDateTime.now(),
                null,
                2L,
                "url2"
            )
            every { groupPurchaseJpqlRepository.findAllWithCursorAndParticipantCount(cursorId, progressStatuses, size) } returns listOf(dto1, dto2)

            // when
            val result = service.getAllByCursor(cursorId, progressStatuses, size)

            // then
            result.size shouldBe 2
            result[0].title shouldBe "제목1"
            result[1].title shouldBe "제목2"

            verify(exactly = 1) { groupPurchaseJpqlRepository.findAllWithCursorAndParticipantCount(cursorId, progressStatuses, size) }
        }
    }



}