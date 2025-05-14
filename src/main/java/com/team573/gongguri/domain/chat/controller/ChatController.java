package com.team573.gongguri.domain.chat.controller;

import com.team573.gongguri.domain.chat.dto.ChatMessageRequestDto;
import com.team573.gongguri.domain.chat.dto.ChatMessageResponseDto;
import com.team573.gongguri.domain.chat.service.ChatService;
import com.team573.gongguri.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class ChatController {

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

    @GetMapping("/group-purchases/{groupPurchaseId}/chat")
    public String groupPurchaseChat(
        @PathVariable Long groupPurchaseId,
        Model model,
        @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        Long roomId = chatService.getChatRoomIdByGroupPurchaseId(groupPurchaseId);

        model.addAttribute("nickname", customUserDetails.getNickname());
        model.addAttribute("roomId", roomId);

        return "chat/chat";
    }
}
