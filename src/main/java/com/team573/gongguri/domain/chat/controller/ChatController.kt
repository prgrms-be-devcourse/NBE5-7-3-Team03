package com.team573.gongguri.domain.chat.controller;

import com.team573.gongguri.domain.chat.dto.ChatMessageResponseDto;
import com.team573.gongguri.domain.chat.service.ChatService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/chat/{roomId}/messages")
    public ResponseEntity<List<ChatMessageResponseDto>> getMessages(
        @PathVariable Long roomId,
        @RequestParam(required = false) String cursor,
        @RequestParam(defaultValue = "20") int size) {

        List<ChatMessageResponseDto> messages = chatService.getMessages(roomId, cursor, size);
        return ResponseEntity.ok(messages);
    }
}
