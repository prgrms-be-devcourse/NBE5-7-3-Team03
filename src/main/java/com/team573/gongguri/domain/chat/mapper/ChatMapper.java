package com.team573.gongguri.domain.chat.mapper;

import com.team573.gongguri.domain.chat.dto.ChatMessageResponseDto;
import com.team573.gongguri.domain.chat.entity.ChatMessage;
import com.team573.gongguri.domain.chat.entity.ChatRoom;
import com.team573.gongguri.domain.chat.entity.ChatRoomParticipation;
import com.team573.gongguri.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatMapper {
    public static ChatMessage toChatMessage(Long roomId, String nickname, String content) {
        return ChatMessage.builder()
            .roomId(roomId)
            .nickname(nickname)
            .content(content)
            .build();
    }

    public static ChatMessageResponseDto toChatMessageResponseDto(ChatMessage chatMessage) {
        return ChatMessageResponseDto.builder()
            .content(chatMessage.getContent())
            .nickname(chatMessage.getNickname())
            .createdAt(chatMessage.getCreatedAt())
            .build();
    }

    public static ChatRoomParticipation toChatRoomParticipation(Member member, ChatRoom chatRoom) {
        return ChatRoomParticipation.builder()
            .member(member)
            .chatRoom(chatRoom)
            .build();
    }
}
