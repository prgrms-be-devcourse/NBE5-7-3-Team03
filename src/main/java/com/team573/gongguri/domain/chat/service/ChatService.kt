package com.team573.gongguri.domain.chat.service;

import static com.team573.gongguri.global.exception.CustomErrorCode.*;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.team573.gongguri.domain.chat.dto.ChatMessageRequestDto;
import com.team573.gongguri.domain.chat.dto.ChatMessageResponseDto;
import com.team573.gongguri.domain.chat.entity.ChatMessage;
import com.team573.gongguri.domain.chat.entity.ChatRoom;
import com.team573.gongguri.domain.chat.entity.ChatRoomParticipation;
import com.team573.gongguri.domain.chat.mapper.ChatMessageMapperKt;
import com.team573.gongguri.domain.chat.mapper.ChatRoomMapperKt;
import com.team573.gongguri.domain.chat.repository.ChatMessageRepository;
import com.team573.gongguri.domain.chat.repository.ChatRoomParticipationRepository;
import com.team573.gongguri.domain.chat.repository.ChatRoomRepository;
import com.team573.gongguri.domain.chat.repository.CustomChatMessageRepository;
import com.team573.gongguri.domain.member.entity.Member;
import com.team573.gongguri.domain.member.repository.MemberRepository;
import com.team573.gongguri.global.exception.CustomException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomParticipationRepository chatRoomParticipationRepository;
    private final CustomChatMessageRepository customChatMessageRepository;

    // 채팅 메세지 저장
    public ChatMessageResponseDto addChatMessage(
        Long roomId,
        ChatMessageRequestDto requestDto
    ) {
        ChatMessage createdMessage
            = ChatMessageMapperKt.toChatMessage(roomId, requestDto.getNickname(), requestDto.getContent());

        ChatMessage saved = chatMessageRepository.save(createdMessage);

        return ChatMessageMapperKt.toDto(saved);
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
            .orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER));

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
            .orElseThrow(() -> new CustomException(NOT_FOUND_CHATROOM));

        ChatRoomParticipation createdParticipation = ChatRoomMapperKt.toParticipationEntity(member, chatRoom);

        chatRoomParticipationRepository.save(createdParticipation);
    }

    // 채팅방 참여자 제거
    public void deleteChatParticipation(Long roomId, Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER));

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
            .orElseThrow(() -> new CustomException(NOT_FOUND_CHATROOM));

        chatRoomParticipationRepository.deleteByChatRoomAndMember(chatRoom, member);
    }

    // 채팅방 찾기
    public Long getChatRoomIdByGroupPurchaseId(Long groupPurchaseId) {
        ChatRoom findChatRoom = chatRoomRepository.findChatRoomByGroupId(groupPurchaseId);
        return findChatRoom.getChatRoomId();
    }

    // 이전 채팅 메시지 조회
    public List<ChatMessageResponseDto> getMessages(Long roomId, String cursor, int size) {
        PageRequest pageRequest = PageRequest.of(0, size);
        List<ChatMessage> messages;

        if (cursor == null || cursor.isBlank()) {
            // 최신 메시지 조회
            messages = chatMessageRepository.findLatestByRoomId(roomId, pageRequest);
        } else {
            // 커서 이전 메시지 조회
            ObjectId cursorId = new ObjectId(cursor);
            messages = chatMessageRepository.findLatestByRoomIdAndCursor(roomId, cursorId, pageRequest);
        }

        return messages.stream()
            .map(ChatMessageMapperKt::toDto)
            .toList();
    }

    // 가장 최근 메시지 조회
    public Map<Long, String> getFirstMessageMap(List<Long> chatRoomIds) {
        return customChatMessageRepository.findLatestMessageByRoomIds(chatRoomIds);
    }
}
