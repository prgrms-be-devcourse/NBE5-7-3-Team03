package com.team573.gongguri.domain.chat.controller;

import com.team573.gongguri.domain.chat.service.ChatService;
import com.team573.gongguri.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class ChatViewController {

    private final ChatService chatService;

    @GetMapping("/group-purchase/{groupPurchaseId}/chat")
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
