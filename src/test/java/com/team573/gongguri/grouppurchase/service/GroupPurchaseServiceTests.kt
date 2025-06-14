package com.team573.gongguri.grouppurchase.service

import com.team573.gongguri.domain.chat.entity.ChatRoom
import com.team573.gongguri.domain.chat.service.ChatService
import com.team573.gongguri.domain.grouppurchase.dto.GroupPurchaseRequestDto
import com.team573.gongguri.domain.grouppurchase.dto.GroupPurchaseWithParticipantCountDto
import com.team573.gongguri.domain.grouppurchase.entity.GroupPurchase
import com.team573.gongguri.domain.grouppurchase.entity.ParticipationStatus
import com.team573.gongguri.domain.grouppurchase.entity.ProgressStatus
import com.team573.gongguri.domain.grouppurchase.repository.GroupPurchaseJpqlRepository
import com.team573.gongguri.domain.grouppurchase.repository.GroupPurchaseParticipantRepository
import com.team573.gongguri.domain.grouppurchase.repository.GroupPurchaseRepository
import com.team573.gongguri.domain.grouppurchase.service.GroupPurchaseService
import com.team573.gongguri.domain.member.entity.Member
import com.team573.gongguri.domain.member.entity.Univ
import com.team573.gongguri.domain.member.repository.MemberRepository
import com.team573.gongguri.domain.member.service.MemberService
import com.team573.gongguri.global.exception.CustomErrorCode
import com.team573.gongguri.global.exception.CustomException
import io.kotest.matchers.shouldBe
import io.mockk.*
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.util.ReflectionTestUtils
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockKExtension::class)
class GroupPurchaseServiceTests {
    private val groupPurchaseRepository: GroupPurchaseRepository = mockk()
    private val chatService: ChatService = mockk()
    private val memberRepository: MemberRepository = mockk()
    private val groupPurchaseJpqlRepository: GroupPurchaseJpqlRepository = mockk()
    private val groupPurchaseParticipantRepository: GroupPurchaseParticipantRepository = mockk()
    private val memberService: MemberService = mockk()

    private val groupPurchaseService = GroupPurchaseService(
        groupPurchaseRepository,
        memberRepository,
        chatService,
        groupPurchaseJpqlRepository,
        groupPurchaseParticipantRepository,
        memberService
    )
    private lateinit var member: Member
    private lateinit var univ: Univ
    private lateinit var chatRoom: ChatRoom
    private lateinit var groupPurchase: GroupPurchase
    private lateinit var groupPurchaseRequestDto: GroupPurchaseRequestDto

    private val memberId = 1L

    @BeforeEach
    fun setUp() {
        univ = Univ("데브대학교")
        member = Member(
            memberId,
            univ,
            "test@example.com",
            "Tester",
            "1234",
            0,
            0
        )
        chatRoom = ChatRoom(99L)
        groupPurchase = GroupPurchase(
            null,  // groupId는 @GeneratedValue이므로 null로 시작
            member,
            univ,
            chatRoom,
            ProgressStatus.RECRUITING,
            "라면 나눠요",
            "라면 10묶음 싸게 구입해서 나눠가집시다!",
            10000,
            5,
            "카카오뱅크",
            "111-1111-1111-11",
            "https://image.com/socks.jpg",
            false
        )

        groupPurchaseRequestDto = GroupPurchaseRequestDto(
            "라면 나눠요",
            "라면 10묶음 싸게 구입해서 나눠가집시다!",
            10000,
            5,
            "카카오뱅크",
            "111-1111-1111-11",
            "RECRUITING",
            "https://image.com/socks.jpg"
        )
        //@GeneratedValue 때문에 테스트 용도로 강제 주입
        ReflectionTestUtils.setField(member, "memberId", 1L)
        ReflectionTestUtils.setField(groupPurchase, "groupId", 100L)
    }


    @Test
    @DisplayName("공동구매 생성 성공 테스트")
    fun addGroupPurchaseTests_successfully() {
        //given
        every { memberService.getMemberById(memberId) } returns member
        every { chatService.addChatRoom(member.email) } returns chatRoom
        every { groupPurchaseRepository.save(any()) } returns groupPurchase
        every { groupPurchaseParticipantRepository.save(any()) } returns mockk()

        //when
        val result = groupPurchaseService.add(groupPurchaseRequestDto, memberId)

        // then
        result.title shouldBe "라면 나눠요"
        result.id shouldBe 100L
        result.progressStatus shouldBe "RECRUITING"

        verify(exactly = 1) { memberService.getMemberById(memberId) }
        verify(exactly = 1) { chatService.addChatRoom(member.email) }
        verify(exactly = 1) { groupPurchaseRepository.save(any()) }
    }


    @Test
    @DisplayName("공동구매 상세 조회 성공 테스트")
    fun getGroupPurchaseTests_successfully() {
        // given
        every { groupPurchaseRepository.findById(groupPurchase.groupId!!) } returns Optional.of(groupPurchase)
        every { groupPurchaseParticipantRepository.countByGroupPurchaseAndParticipationStatus(groupPurchase, ParticipationStatus.JOINED) } returns 2L
        every { groupPurchaseParticipantRepository.existsByGroupPurchase_GroupIdAndMember_MemberId(groupPurchase.groupId!!, memberId) } returns true

        // when
        val result = groupPurchaseService.get(groupPurchase.groupId!!, memberId)

        // then
        result.title shouldBe "라면 나눠요"
        result.isParticipated shouldBe true
        result.currentParticipants shouldBe 2L

        verify(exactly = 1) { groupPurchaseRepository.findById(groupPurchase.groupId!!) }
        verify { groupPurchaseParticipantRepository.countByGroupPurchaseAndParticipationStatus(groupPurchase, ParticipationStatus.JOINED) }
        verify { groupPurchaseParticipantRepository.existsByGroupPurchase_GroupIdAndMember_MemberId(groupPurchase.groupId!!, memberId) }
    }


    @Test
    @DisplayName("공동구매 수정 성공 테스트")
    fun updateGroupPurchaseTests_successfully() {
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
        every { groupPurchaseRepository.findByGroupIdAndDeletedFalse(groupPurchase.groupId!!) } returns groupPurchase

        // when
        val result = groupPurchaseService.update(groupPurchase.groupId!!, updateDto)

        // then
        result.title shouldBe "수정된 제목"
        result.progressStatus shouldBe "CLOSED"
        result.imageUrl shouldBe "https://newimage.com/item.jpg"

        // 검증
        verify(exactly = 1) { groupPurchaseRepository.findByGroupIdAndDeletedFalse(groupPurchase.groupId!!) }
    }

    @Test
    @DisplayName("공동구매 삭제 성공 테스트")
    fun deleteGroupPurchaseTests_successfully() {
        // given
        every { groupPurchaseRepository.findByGroupIdAndDeletedFalse(groupPurchase.groupId!!) } returns groupPurchase
        every { groupPurchaseParticipantRepository.existsByGroupPurchase_GroupIdAndDepositIsTrue(groupPurchase.groupId!!) } returns false

        // when
        groupPurchaseService.delete(groupPurchase.groupId!!, memberId)

        // then
        groupPurchase.deleted shouldBe true

        // 검증
        verify(exactly = 1) { groupPurchaseRepository.findByGroupIdAndDeletedFalse(groupPurchase.groupId!!) }
        verify(exactly = 1) { groupPurchaseParticipantRepository.existsByGroupPurchase_GroupIdAndDepositIsTrue(groupPurchase.groupId!!) }
    }

    @Test
    @DisplayName("공동구매 참여 성공 테스트")
    fun joinGroupPurchaseTests_successfully() {
        // given
        every { memberService.getMemberById(memberId) } returns member
        every { groupPurchaseParticipantRepository.existsByGroupPurchase_GroupIdAndMember_MemberId(groupPurchase.groupId!!, memberId) } returns false
        every { groupPurchaseRepository.findByGroupIdAndDeletedFalse(groupPurchase.groupId!!) } returns groupPurchase
        every { groupPurchaseParticipantRepository.countByGroupPurchaseAndParticipationStatus(groupPurchase, ParticipationStatus.JOINED) } returns 1L
        every { chatService.addChatParticipation(groupPurchase.chatRoom.chatRoomId!!, member.email) } just Runs
        every { groupPurchaseParticipantRepository.save(any()) } returns mockk()

        // when
        groupPurchaseService.join(groupPurchase.groupId!!, memberId)

        // then
        groupPurchase.progressStatus shouldBe ProgressStatus.RECRUITING

        // 검증
        verify(exactly = 1) { chatService.addChatParticipation(groupPurchase.chatRoom.chatRoomId!!, member.email) }
        verify(exactly = 1) { groupPurchaseParticipantRepository.save(any()) }
    }

    @Test
    @DisplayName("이미 공동구매에 참여중인 경우 예외 테스트")
    fun joinGroupPurchaseTests_alreadyJoined_theowsException() {
        // given
        every { memberService.getMemberById(memberId) } returns member
        every { groupPurchaseParticipantRepository.existsByGroupPurchase_GroupIdAndMember_MemberId(groupPurchase.groupId!!, memberId) } returns true
        every { groupPurchaseRepository.findByGroupIdAndDeletedFalse(groupPurchase.groupId!!) } returns groupPurchase
        every { groupPurchaseParticipantRepository.countByGroupPurchaseAndParticipationStatus(groupPurchase, ParticipationStatus.JOINED) } returns 1L

        // when & then
        val exception = assertThrows<CustomException> { groupPurchaseService.join(groupPurchase.groupId!!, memberId) }

        // 검증
        exception.getCustomErrorCode() shouldBe CustomErrorCode.ALREADY_JOINED
    }

    @Test
    @DisplayName("공동구매 참여 인원 초과 예외 테스트")
    fun joinGroupPurchaseTests_participantLimitExceeded_theowsException() {
        // given
        every { memberService.getMemberById(memberId) } returns member
        every { groupPurchaseRepository.findByGroupIdAndDeletedFalse(groupPurchase.groupId!!) } returns groupPurchase
        every {
            groupPurchaseParticipantRepository.countByGroupPurchaseAndParticipationStatus(
                groupPurchase, ParticipationStatus.JOINED
            )
        } returns groupPurchase.maxParticipants.toLong()

        // when & then
        val exception = assertThrows<CustomException> { groupPurchaseService.join(groupPurchase.groupId!!, memberId) }

        //검증
        exception.getCustomErrorCode() shouldBe CustomErrorCode.PARTICIPANT_LIMIT_REACHED
    }

    @Test
    @DisplayName("참여 후 정원이 가득 차면 상태가 CLOSED로 변경 테스트")
    fun joinGroupPurchaseTests_reachesMaxParticipants_theowsException() {
        // given
        every { memberService.getMemberById(memberId) } returns member
        every { groupPurchaseRepository.findByGroupIdAndDeletedFalse(groupPurchase.groupId!!) } returns groupPurchase
        every { groupPurchaseParticipantRepository.countByGroupPurchaseAndParticipationStatus(groupPurchase, ParticipationStatus.JOINED) } returns (groupPurchase.maxParticipants - 1).toLong()
        every { groupPurchaseParticipantRepository.existsByGroupPurchase_GroupIdAndMember_MemberId(groupPurchase.groupId!!, memberId) } returns false
        every { chatService.addChatParticipation(any(), any()) } returns Unit
        every { groupPurchaseParticipantRepository.save(any()) } returns mockk()

        // when
        groupPurchaseService.join(groupPurchase.groupId!!, memberId)

        // then
        groupPurchase.progressStatus shouldBe ProgressStatus.CLOSED
    }

    @Test
    @DisplayName("커서 기반 공동구매 조회 성공 테스트")
    fun allGroupPurchaseTests_successfully() {
        // given
        val cursorId: Long? = null
        val progressStatuses = listOf(ProgressStatus.RECRUITING)
        val size = 3

        val dto1 = GroupPurchaseWithParticipantCountDto(
            101L, "제목1", "내용1", 10000, 5,
            ProgressStatus.RECRUITING, LocalDateTime.now(), null, 1L, "url1"
        )
        val dto2 = GroupPurchaseWithParticipantCountDto(
            102L, "제목2", "내용2", 20000, 3,
            ProgressStatus.RECRUITING, LocalDateTime.now(), null, 2L, "url2"
        )

        every {
            groupPurchaseJpqlRepository!!.findAllWithCursorAndParticipantCount(cursorId, progressStatuses, size)
        } returns listOf(dto1, dto2)

        // when
        val result = groupPurchaseService!!.getAllByCursor(cursorId, progressStatuses, size)

        // then
        result.size shouldBe 2
        result[0].title shouldBe "제목1"
        result[1].title shouldBe "제목2"

        verify(exactly = 1) {
            groupPurchaseJpqlRepository!!.findAllWithCursorAndParticipantCount(cursorId, progressStatuses, size)
        }
    }

}
