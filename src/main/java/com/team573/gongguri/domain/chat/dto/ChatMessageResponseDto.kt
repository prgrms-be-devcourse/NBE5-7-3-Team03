package com.team573.gongguri.domain.chat.dto

import java.time.LocalDateTime

data class ChatMessageResponseDto(
    val messageId: String,
    val nickname: String,
    val content: String,
    val createdAt: LocalDateTime
)
