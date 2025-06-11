package com.team573.gongguri.domain.chat.mapper;

import com.team573.gongguri.domain.chat.dto.ChatMessageResponseDto;
import com.team573.gongguri.domain.chat.entity.ChatMessage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatMessageMapper {
    public static ChatMessage toChatMessage(Long roomId, String nickname, String content) {
        return ChatMessage.builder()
            .roomId(roomId)
            .nickname(nickname)
            .content(content)
            .build();
    }

    public static ChatMessageResponseDto toDto(ChatMessage chatMessage) {
        return ChatMessageResponseDto.builder()
            .messageId(chatMessage.getId().toHexString())
            .content(chatMessage.getContent())
            .nickname(chatMessage.getNickname())
            .createdAt(chatMessage.getCreatedAt())
            .build();
    }
}
