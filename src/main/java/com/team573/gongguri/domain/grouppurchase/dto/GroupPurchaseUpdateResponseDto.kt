package com.team573.gongguri.domain.grouppurchase.dto

data class GroupPurchaseUpdateResponseDto(
    val id: Long,
    val title: String,
    val content: String,
    val price: Int,
    val maxParticipants: Int,
    val progressStatus: String,
    val imageUrl: String
)
