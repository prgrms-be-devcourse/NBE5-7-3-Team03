package com.team573.gongguri.domain.grouppurchase.dto

import com.team573.gongguri.domain.grouppurchase.entity.ProgressStatus
import java.time.LocalDateTime

data class GroupPurchaseWithParticipantCountDto(
    val groupId: Long,
    val title: String,
    val content: String,
    val price: Int,
    val maxParticipants: Int,
    val progressStatus: ProgressStatus,
    val createdAt: LocalDateTime,
    val chatRoomId: Long?,
    val participantCount: Long,
    val imageUrl: String
)
