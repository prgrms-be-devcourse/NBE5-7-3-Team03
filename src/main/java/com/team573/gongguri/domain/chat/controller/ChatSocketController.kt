package com.team573.gongguri.domain.chat.controller

import com.team573.gongguri.domain.chat.dto.ChatMessageRequestDto
import com.team573.gongguri.domain.chat.dto.ChatMessageResponseDto
import com.team573.gongguri.domain.chat.service.ChatService
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller

@Controller
class ChatSocketController(
    private val chatService: ChatService
) {

    @MessageMapping("/{roomId}")
    @SendTo("/room/{roomId}")
    fun sendMessage(
        @DestinationVariable roomId: Long,
        requestDto: ChatMessageRequestDto
    ): ChatMessageResponseDto {
        // DB에 채팅 저장
        return chatService.addChatMessage(roomId, requestDto)
    }
}
