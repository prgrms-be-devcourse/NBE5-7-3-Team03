package com.team573.gongguri.domain.chat.controller;

import com.team573.gongguri.domain.chat.dto.ChatMessageRequestDto;
import com.team573.gongguri.domain.chat.dto.ChatMessageResponseDto;
import com.team573.gongguri.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatSocketController {

    private final ChatService chatService;

    @MessageMapping("/{roomId}")
    @SendTo("/room/{roomId}")
    public ChatMessageResponseDto sendMessage(
        @DestinationVariable Long roomId,
        ChatMessageRequestDto requestDto
    ) {
        // DB에 채팅 저장
        return chatService.addChatMessage(roomId, requestDto);
    }
}
