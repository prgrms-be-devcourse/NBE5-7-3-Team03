package com.team573.gongguri.domain.grouppurchase.dto

data class GroupPurchaseListResponseDto(
    val id: Long,
    val title: String,
    val price: Int,
    val maxParticipants: Int,
    val currentParticipants: Long,
    val progressStatus: String,
    val imageUrl: String
)
