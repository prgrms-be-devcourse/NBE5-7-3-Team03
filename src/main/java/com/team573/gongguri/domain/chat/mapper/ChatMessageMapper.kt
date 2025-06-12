package com.team573.gongguri.domain.chat.mapper

import com.team573.gongguri.domain.chat.dto.ChatMessageResponseDto
import com.team573.gongguri.domain.chat.entity.ChatMessage

fun toChatMessage(roomId: Long, nickname: String, content: String): ChatMessage {
    return ChatMessage(
        roomId = roomId,
        nickname = nickname,
        content = content
    )
}


fun toDto(chatMessage: ChatMessage): ChatMessageResponseDto {
    return ChatMessageResponseDto(
        messageId = chatMessage.id!!.toHexString(),
        content = chatMessage.content,
        nickname = chatMessage.nickname,
        createdAt = chatMessage.createdAt
    )
}
