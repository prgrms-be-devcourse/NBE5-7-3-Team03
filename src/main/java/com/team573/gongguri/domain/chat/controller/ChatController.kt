package com.team573.gongguri.domain.chat.controller

import com.team573.gongguri.domain.chat.dto.ChatMessageResponseDto
import com.team573.gongguri.domain.chat.service.ChatService
import lombok.RequiredArgsConstructor
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequiredArgsConstructor
class ChatController {
    private val chatService: ChatService? = null

    @GetMapping("/chat/{roomId}/messages")
    fun getMessages(
        @PathVariable roomId: Long?,
        @RequestParam(required = false) cursor: String?,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<List<ChatMessageResponseDto>> {
        val messages = chatService!!.getMessages(roomId, cursor, size)
        return ResponseEntity.ok(messages)
    }
}
