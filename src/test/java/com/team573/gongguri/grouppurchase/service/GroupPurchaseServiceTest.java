package com.team573.gongguri.grouppurchase.service;

import com.team573.gongguri.domain.chat.entity.ChatRoom;
import com.team573.gongguri.domain.chat.service.ChatService;
import com.team573.gongguri.domain.grouppurchase.dto.GroupPurchaseCreateResponseDto;
import com.team573.gongguri.domain.grouppurchase.dto.GroupPurchaseRequestDto;
import com.team573.gongguri.domain.grouppurchase.entity.GroupPurchase;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
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
}
