package com.team573.gongguri.domain.grouppurchase.dto

import java.time.LocalDateTime

data class GroupPurchaseWithChatResponseDto(
    val id: Long,
    val participantId: Long,
    val title: String,
    val maxParticipants: Int,
    val participantCount: Long,
    val progressStatus: String,
    val imageUrl: String,
    val chatMessage: String?,
    val createAt: LocalDateTime
)
