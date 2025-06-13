package com.team573.gongguri.grouppurchase.service;

import com.team573.gongguri.domain.chat.entity.ChatRoom;
import com.team573.gongguri.domain.chat.service.ChatService;
import com.team573.gongguri.domain.grouppurchase.dto.GroupPurchaseCreateResponseDto;
import com.team573.gongguri.domain.grouppurchase.dto.GroupPurchaseRequestDto;
import com.team573.gongguri.domain.grouppurchase.dto.GroupPurchaseUpdateResponseDto;
import com.team573.gongguri.domain.grouppurchase.entity.GroupPurchase;
import com.team573.gongguri.domain.grouppurchase.entity.ParticipationStatus;
import com.team573.gongguri.domain.grouppurchase.entity.ProgressStatus;
import com.team573.gongguri.domain.grouppurchase.mapper.GroupPurchaseMapperKt;
import com.team573.gongguri.domain.grouppurchase.repository.GroupPurchaseJpqlRepository;
import com.team573.gongguri.domain.grouppurchase.repository.GroupPurchaseParticipantRepository;
import com.team573.gongguri.domain.grouppurchase.repository.GroupPurchaseRepository;
import com.team573.gongguri.domain.grouppurchase.service.GroupPurchaseService;
import com.team573.gongguri.domain.member.entity.Member;
import com.team573.gongguri.domain.member.entity.Univ;
import com.team573.gongguri.domain.member.repository.MemberRepository;
import com.team573.gongguri.domain.member.service.MemberService;
import com.team573.gongguri.global.exception.CustomErrorCode;
import com.team573.gongguri.global.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GroupPurchaseServiceTest {

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
    }


    @Test
    void add() {
        //given
        when(memberService.getMemberById(memberId)).thenReturn(member);
        when(chatService.addChatRoom(member.getEmail())).thenReturn(chatRoom);
        // DB 저장 후 ID가 생성된 새로운 객체를 반환하도록 모킹
        GroupPurchase savedGroupPurchase = new GroupPurchase(
                100L,  // 저장 후 생성된 ID
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
        when(groupPurchaseRepository.save(any(GroupPurchase.class))).thenReturn(savedGroupPurchase);

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
    void update() {
        // given
        Long groupId = 100L;

        GroupPurchase existingPurchase = new GroupPurchase(
                groupId,
                member,
                univ,
                chatRoom,
                ProgressStatus.RECRUITING,
                "기존 제목",
                "기존 내용",
                5000,
                3,
                "국민은행",
                "222-2222-2222-22",
                "https://oldimage.com/item.jpg",
                false
        );

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

        when(groupPurchaseRepository.findByGroupIdAndDeletedFalse(groupId)).thenReturn(existingPurchase);

        // when
        GroupPurchaseUpdateResponseDto result = groupPurchaseService.update(groupId, updateDto);

        // then
        assertNotNull(result);
        assertEquals("수정된 제목", result.getTitle());
        assertEquals("수정된 내용", result.getContent());
        assertEquals(12000, result.getPrice());
        assertEquals(6, result.getMaxParticipants());
        assertEquals("CLOSED", result.getProgressStatus());
        assertEquals("https://newimage.com/item.jpg", result.getImageUrl());

        // 검증
        verify(groupPurchaseRepository).findByGroupIdAndDeletedFalse(groupId);
    }

    @Test
    void delete() {
        // given
        Long groupId = 100L;
        Long memberId = this.memberId;

        when(groupPurchaseRepository.findByGroupIdAndDeletedFalse(groupId)).thenReturn(groupPurchase);
        when(groupPurchaseParticipantRepository.existsByGroupPurchase_GroupIdAndDepositIsTrue(groupId)).thenReturn(false);

        // when
        groupPurchaseService.delete(groupId, memberId);

        // then
        assertTrue(groupPurchase.getDeleted()); // markAsDeleted()가 정상 호출되었는지 확인

        verify(groupPurchaseRepository).findByGroupIdAndDeletedFalse(groupId);
        verify(groupPurchaseParticipantRepository).existsByGroupPurchase_GroupIdAndDepositIsTrue(groupId);
    }

    @Test
    void join() {
        //given
        Long groupId = 100L;
        Long memberId = this.memberId;
        when(memberService.getMemberById(memberId)).thenReturn(member);
        when(groupPurchaseParticipantRepository.existsByGroupPurchase_GroupIdAndMember_MemberId(groupId, memberId)).thenReturn(false);
        when(groupPurchaseRepository.findByGroupIdAndDeletedFalse(groupId)).thenReturn(groupPurchase);
        when(groupPurchaseParticipantRepository.countByGroupPurchaseAndParticipationStatus(groupPurchase, ParticipationStatus.JOINED)).thenReturn(1L);

        //when
        groupPurchaseService.join(groupId, memberId);

        //then
        assertEquals(ProgressStatus.RECRUITING, groupPurchase.getProgressStatus());
        verify(chatService).addChatParticipation(groupPurchase.getChatRoom().getChatRoomId(), member.getEmail());
        verify(groupPurchaseParticipantRepository).save(any());

    }

    @Test
    @DisplayName("이미 공동구매에 참여중인 경우 확인 테스트")
    void AlreadyJoined() {
        //given
        Long groupId = 100L;
        Long memberId = this.memberId;
        when(memberService.getMemberById(memberId)).thenReturn(member);
        when(groupPurchaseParticipantRepository.existsByGroupPurchase_GroupIdAndMember_MemberId(groupId, memberId)).thenReturn(true);
        when(groupPurchaseRepository.findByGroupIdAndDeletedFalse(groupId)).thenReturn(groupPurchase);
        when(groupPurchaseParticipantRepository.countByGroupPurchaseAndParticipationStatus(groupPurchase, ParticipationStatus.JOINED)).thenReturn(1L);

        //when & then
        assertThrows(CustomException.class, () -> groupPurchaseService.join(groupId, memberId));
    }

    @Test
    @DisplayName("이미 공동구매에 참여중인 경우 확인 테스트")
    void AlreadyJoined_successfully() {
        //given
        Long groupId = 100L;
        Long memberId = this.memberId;
        when(memberService.getMemberById(memberId)).thenReturn(member);
        when(groupPurchaseParticipantRepository.existsByGroupPurchase_GroupIdAndMember_MemberId(groupId, memberId)).thenReturn(true);
        when(groupPurchaseRepository.findByGroupIdAndDeletedFalse(groupId)).thenReturn(groupPurchase);
        when(groupPurchaseParticipantRepository.countByGroupPurchaseAndParticipationStatus(groupPurchase, ParticipationStatus.JOINED)).thenReturn(1L);

        //when & then
        assertThrows(CustomException.class, () -> groupPurchaseService.join(groupId, memberId));
    }

//    @Test
//    @DisplayName("")
//    void
}
