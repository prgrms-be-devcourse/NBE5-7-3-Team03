package com.team573.gongguri.domain.grouppurchase.dto

import com.team573.gongguri.domain.grouppurchase.entity.ProgressStatus

data class GroupPurchaseSimpleResponseDto(
    val id: Long,
    val title: String,
    val maxParticipants: Int,
    val participantCount: Long,
    val progressStatus: ProgressStatus,
    val imageUrl: String,
    val price: Int
)
