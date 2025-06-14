package com.team573.gongguri.grouppurchase.service;

import com.team573.gongguri.domain.chat.entity.ChatRoom;
import com.team573.gongguri.domain.chat.service.ChatService;
import com.team573.gongguri.domain.grouppurchase.dto.*;
import com.team573.gongguri.domain.grouppurchase.entity.GroupPurchase;
import com.team573.gongguri.domain.grouppurchase.entity.ParticipationStatus;
import com.team573.gongguri.domain.grouppurchase.entity.ProgressStatus;
import com.team573.gongguri.domain.grouppurchase.repository.GroupPurchaseJpqlRepository;
import com.team573.gongguri.domain.grouppurchase.repository.GroupPurchaseParticipantRepository;
import com.team573.gongguri.domain.grouppurchase.repository.GroupPurchaseRepository;
import com.team573.gongguri.domain.grouppurchase.service.GroupPurchaseService;
import com.team573.gongguri.domain.member.entity.Member;
import com.team573.gongguri.domain.member.entity.Univ;
import com.team573.gongguri.domain.member.repository.MemberRepository;
import com.team573.gongguri.domain.member.service.MemberService;
import com.team573.gongguri.global.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GroupPurchaseServiceTests {

    @Mock
    private GroupPurchaseRepository groupPurchaseRepository;

    @Mock
    private ChatService chatService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private GroupPurchaseJpqlRepository groupPurchaseJpqlRepository;

    @Mock
    private GroupPurchaseParticipantRepository groupPurchaseParticipantRepository;

    @Mock
    private MemberService memberService;

    @InjectMocks
    private GroupPurchaseService groupPurchaseService;

    private Member member;
    private Univ univ;
    private ChatRoom chatRoom;
    private GroupPurchase groupPurchase;
    private GroupPurchaseRequestDto groupPurchaseRequestDto;

    private final Long memberId = 1L;
    /*
    *
    * */
    @BeforeEach
    public void SetUp() {
        univ = new Univ("데브대학교");
        member = new Member(memberId, univ, "test@example.com", "Tester", "1234", 0, 0);
        chatRoom = new ChatRoom(99L);
        groupPurchase = new GroupPurchase(
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
        );

        groupPurchaseRequestDto = new GroupPurchaseRequestDto(
                "라면 나눠요",
                "라면 10묶음 싸게 구입해서 나눠가집시다!",
                10000,
                5,
                "카카오뱅크",
                "111-1111-1111-11",
                "RECRUITING",
                "https://image.com/socks.jpg"
        );
        //@GeneratedValue 때문에 테스트 용도로 강제 주입
        ReflectionTestUtils.setField(member, "memberId", 1L);
        ReflectionTestUtils.setField(groupPurchase, "groupId", 100L);
    }


    @Test
    @DisplayName("공동구매 생성 성공 테스트")
    void addGroupPurchaseTests_successfully() {
        //given
        when(memberService.getMemberById(memberId)).thenReturn(member);
        when(chatService.addChatRoom(member.getEmail())).thenReturn(chatRoom);
        when(groupPurchaseRepository.save(any(GroupPurchase.class))).thenReturn(groupPurchase);

        //when
        GroupPurchaseCreateResponseDto result = groupPurchaseService.add(groupPurchaseRequestDto, memberId);

        //then
        assertNotNull(result);
        assertEquals("라면 나눠요", result.getTitle());
        assertEquals(100L, result.getId());
        assertEquals("RECRUITING", result.getProgressStatus());

        // 검증: 메서드들이 올바르게 호출되었는지 확인
        verify(memberService).getMemberById(memberId);
        verify(chatService).addChatRoom(member.getEmail());
        verify(groupPurchaseRepository).save(any(GroupPurchase.class));
    }

    @Test
    @DisplayName("공동구매 상세 조회 성공 테스트")
    void getGroupPurchaseTests_successfully(){
        //given

        when(groupPurchaseRepository.findById(groupPurchase.getGroupId())).thenReturn(Optional.of(groupPurchase));
        when(groupPurchaseParticipantRepository.countByGroupPurchaseAndParticipationStatus(groupPurchase, ParticipationStatus.JOINED)).thenReturn(2L);
        when(groupPurchaseParticipantRepository.existsByGroupPurchase_GroupIdAndMember_MemberId(groupPurchase.getGroupId(), memberId)).thenReturn(true);

        //when
        var result = groupPurchaseService.get(groupPurchase.getGroupId(), memberId);

        // then
        assertNotNull(result);
        assertEquals("라면 나눠요", result.getTitle()); // 필요 시 더 구체적으로 검증 가능
        assertTrue(result.isParticipated());
        assertEquals(2L, result.getCurrentParticipants());

       // 검증
        verify(groupPurchaseRepository).findById(groupPurchase.getGroupId());
        verify(groupPurchaseParticipantRepository).countByGroupPurchaseAndParticipationStatus(groupPurchase, ParticipationStatus.JOINED);
        verify(groupPurchaseParticipantRepository).existsByGroupPurchase_GroupIdAndMember_MemberId(groupPurchase.getGroupId(), memberId);

    }

    @Test
    @DisplayName("공동구매 수정 성공 테스트")
    void updateGroupPurchaseTests_successfully() {
        // given
        GroupPurchaseRequestDto updateDto = new GroupPurchaseRequestDto(
                "수정된 제목",
                "수정된 내용",
                12000,
                6,
                "신한은행",
                "333-3333-3333-33",
                "CLOSED",
                "https://newimage.com/item.jpg"
        );
        when(groupPurchaseRepository.findByGroupIdAndDeletedFalse(groupPurchase.getGroupId())).thenReturn(groupPurchase);

        // when
        GroupPurchaseUpdateResponseDto result = groupPurchaseService.update(groupPurchase.getGroupId(), updateDto);

        // then
        assertNotNull(result);
        assertEquals("수정된 제목", result.getTitle());
        assertEquals("CLOSED", result.getProgressStatus());
        assertEquals("https://newimage.com/item.jpg", result.getImageUrl());

        // 검증
        verify(groupPurchaseRepository).findByGroupIdAndDeletedFalse(groupPurchase.getGroupId());
    }

    @Test
    @DisplayName("공동구매 삭제 성공 테스트")
    void deleteGroupPurchaseTests_successfully() {
        // given
        when(groupPurchaseRepository.findByGroupIdAndDeletedFalse(groupPurchase.getGroupId())).thenReturn(groupPurchase);
        when(groupPurchaseParticipantRepository.existsByGroupPurchase_GroupIdAndDepositIsTrue(groupPurchase.getGroupId())).thenReturn(false);

        // when
        groupPurchaseService.delete(groupPurchase.getGroupId(), memberId);

        // then
        assertTrue(groupPurchase.getDeleted()); // markAsDeleted()가 정상 호출되었는지 확인

        // 검증
        verify(groupPurchaseRepository).findByGroupIdAndDeletedFalse(groupPurchase.getGroupId());
        verify(groupPurchaseParticipantRepository).existsByGroupPurchase_GroupIdAndDepositIsTrue(groupPurchase.getGroupId());
    }

    @Test
    @DisplayName("공동구매 참여 성공 테스트")
    void joinGroupPurchaseTests_successfully() {
        //given
        when(memberService.getMemberById(memberId)).thenReturn(member);
        when(groupPurchaseParticipantRepository.existsByGroupPurchase_GroupIdAndMember_MemberId(groupPurchase.getGroupId(), memberId)).thenReturn(false);
        when(groupPurchaseRepository.findByGroupIdAndDeletedFalse(groupPurchase.getGroupId())).thenReturn(groupPurchase);
        when(groupPurchaseParticipantRepository.countByGroupPurchaseAndParticipationStatus(groupPurchase, ParticipationStatus.JOINED)).thenReturn(1L);

        //when
        groupPurchaseService.join(groupPurchase.getGroupId(), memberId);

        //then
        assertEquals(ProgressStatus.RECRUITING, groupPurchase.getProgressStatus());

        //검증
        verify(chatService).addChatParticipation(groupPurchase.getChatRoom().getChatRoomId(), member.getEmail());
        verify(groupPurchaseParticipantRepository).save(any());

    }

    @Test
    @DisplayName("이미 공동구매에 참여중인 경우 예외 테스트")
    void joinGroupPurchaseTests_alreadyJoined_theowsException() {
        //given
        when(memberService.getMemberById(memberId)).thenReturn(member);
        when(groupPurchaseParticipantRepository.existsByGroupPurchase_GroupIdAndMember_MemberId(groupPurchase.getGroupId(), memberId)).thenReturn(true);
        when(groupPurchaseRepository.findByGroupIdAndDeletedFalse(groupPurchase.getGroupId())).thenReturn(groupPurchase);
        when(groupPurchaseParticipantRepository.countByGroupPurchaseAndParticipationStatus(groupPurchase, ParticipationStatus.JOINED)).thenReturn(1L);

        //when & then
        assertThrows(CustomException.class, () -> groupPurchaseService.join(groupPurchase.getGroupId(), memberId));
    }

    @Test
    @DisplayName("공동구매 참여 인원 초과 예외 테스트")
    void joinGroupPurchaseTests_participantLimitExceeded_theowsException() {
        //given
        when(memberService.getMemberById(memberId)).thenReturn(member);
        when(groupPurchaseRepository.findByGroupIdAndDeletedFalse(groupPurchase.getGroupId())).thenReturn(groupPurchase);
        when(groupPurchaseParticipantRepository.countByGroupPurchaseAndParticipationStatus(groupPurchase, ParticipationStatus.JOINED)).thenReturn((long)groupPurchase.getMaxParticipants());

        //when & then
        assertThrows(CustomException.class, () -> groupPurchaseService.join(groupPurchase.getGroupId(), memberId));
    }

    @Test
    @DisplayName("참여 후 정원이 가득 차면 상태가 CLOSED로 변경 테스트")
    void joinGroupPurchaseTests_reachesMaxParticipants_theowsException() {
        // given
        when(memberService.getMemberById(memberId)).thenReturn(member);
        when(groupPurchaseRepository.findByGroupIdAndDeletedFalse(groupPurchase.getGroupId())).thenReturn(groupPurchase);
        when(groupPurchaseParticipantRepository.countByGroupPurchaseAndParticipationStatus(groupPurchase, ParticipationStatus.JOINED)).thenReturn((long) (groupPurchase.getMaxParticipants() - 1));
        when(groupPurchaseParticipantRepository.existsByGroupPurchase_GroupIdAndMember_MemberId(groupPurchase.getGroupId(), memberId)).thenReturn(false);

        // when
        groupPurchaseService.join(groupPurchase.getGroupId(), memberId);

        // then
        assertEquals(ProgressStatus.CLOSED, groupPurchase.getProgressStatus());
    }

    @Test
    @DisplayName("커서 기반 공동구매 조회 성공 테스트")
    void getAllGroupPurchaseTests_successfully() {
        //given
        Long cursorId = null;
        List<ProgressStatus> progressStatuses = List.of(ProgressStatus.RECRUITING);
        int size = 3;

        GroupPurchaseWithParticipantCountDto dto1 = new GroupPurchaseWithParticipantCountDto(
                101L, "제목1", "내용1", 10000, 5, ProgressStatus.RECRUITING, LocalDateTime.now(), null, 1L, "url1");
        GroupPurchaseWithParticipantCountDto dto2 = new GroupPurchaseWithParticipantCountDto(
                102L, "제목2", "내용2", 20000, 3, ProgressStatus.RECRUITING, LocalDateTime.now(), null, 2L, "url2");
        when(groupPurchaseJpqlRepository.findAllWithCursorAndParticipantCount(cursorId, progressStatuses, size)).thenReturn(List.of(dto1, dto2));

        // when
        List<GroupPurchaseListResponseDto> result = groupPurchaseService.getAllByCursor(cursorId, progressStatuses, size);

        //then
        assertEquals(2, result.size());
        assertEquals("제목1", result.get(0).getTitle());
        assertEquals("제목2", result.get(1).getTitle());

        //검증
        verify(groupPurchaseJpqlRepository).findAllWithCursorAndParticipantCount(cursorId, progressStatuses, size);
    }
}
