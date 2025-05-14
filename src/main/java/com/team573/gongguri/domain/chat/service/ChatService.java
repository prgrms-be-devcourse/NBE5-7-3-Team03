package com.team573.gongguri.domain.chat.service;


import static com.team573.gongguri.domain.chat.mapper.ChatMapper.toChatRoomParticipation;
import static com.team573.gongguri.global.exception.ErrorCode.NOT_FOUND_MEMBER;
import static com.team573.gongguri.global.exception.ErrorCode.NOT_FOUND_ROOM;

import com.team573.gongguri.domain.chat.dto.ChatMessageRequestDto;
import com.team573.gongguri.domain.chat.dto.ChatMessageResponseDto;
import com.team573.gongguri.domain.chat.entity.ChatMessage;
import com.team573.gongguri.domain.chat.entity.ChatRoom;
import com.team573.gongguri.domain.chat.entity.ChatRoomParticipation;
import com.team573.gongguri.domain.chat.mapper.ChatMapper;
import com.team573.gongguri.domain.chat.repository.ChatMessageRepository;
import com.team573.gongguri.domain.chat.repository.ChatRoomParticipationRepository;
import com.team573.gongguri.domain.chat.repository.ChatRoomRepository;
import com.team573.gongguri.domain.member.entity.Member;
import com.team573.gongguri.domain.member.repository.MemberRepository;
import com.team573.gongguri.global.exception.ErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomParticipationRepository chatRoomParticipationRepository;

    // 채팅 메세지 저장
    public ChatMessageResponseDto addChatMessage(
        Long roomId,
        ChatMessageRequestDto requestDto
    ) {
        ChatMessage createdMessage = chatMessageRepository.save(
            ChatMapper.toChatMessage(roomId, requestDto.nickname(), requestDto.content())
        );
        return ChatMapper.toChatMessageResponseDto(createdMessage);
    }

    // 채팅방 생성
    public ChatRoom addChatRoom(String email) {
        ChatRoom chatRoom = chatRoomRepository.save(new ChatRoom());
        addChatParticipation(chatRoom.getChatRoomId(), email);
        return chatRoom;
    }

    // 채팅방 참여자 추가
    public void addChatParticipation(Long roomId, String email) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new ErrorException(NOT_FOUND_MEMBER));

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
            .orElseThrow(() -> new ErrorException(NOT_FOUND_ROOM));

        ChatRoomParticipation createdParticipation = toChatRoomParticipation(member, chatRoom);

        chatRoomParticipationRepository.save(createdParticipation);
    }

    // 채팅방 참여자 제거
    public void deleteChatParticipation(Long roomId, String email) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new ErrorException(NOT_FOUND_MEMBER));

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
            .orElseThrow(() -> new ErrorException(NOT_FOUND_ROOM));

        chatRoomParticipationRepository.deleteByChatRoomAndMember(chatRoom, member);
    }

    // 채팅방 찾기
    public Long getChatRoomIdByGroupPurchaseId(Long groupPurchaseId) {
        ChatRoom findChatRoom = chatRoomRepository.findChatRoomByGroupId(groupPurchaseId);
        return findChatRoom.getChatRoomId();
    }
}
