package com.team573.gongguri.domain.chat.controller

import com.team573.gongguri.domain.chat.service.ChatService
import com.team573.gongguri.domain.grouppurchase.service.GroupPurchaseService
import com.team573.gongguri.global.security.CustomUserDetails
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class ChatViewController(
    private val chatService: ChatService,
    private val groupPurchaseService: GroupPurchaseService
) {

    @GetMapping("/group-purchase/{groupPurchaseId}/chat")
    fun groupPurchaseChat(
        @PathVariable groupPurchaseId: Long,
        model: Model,
        @AuthenticationPrincipal customUserDetails: CustomUserDetails
    ): String {
        val roomId = chatService.getChatRoomIdByGroupPurchaseId(groupPurchaseId)

        model.addAttribute("nickname", customUserDetails.nickname)
        model.addAttribute("roomId", roomId)

        val groupPurchase = groupPurchaseService.getSimpleInfo(groupPurchaseId)

        model.addAttribute("groupPurchase", groupPurchase)

        return "chat/chat"
    }
}
