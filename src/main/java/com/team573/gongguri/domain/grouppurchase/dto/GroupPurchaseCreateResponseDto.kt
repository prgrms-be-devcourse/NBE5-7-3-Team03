package com.team573.gongguri.domain.grouppurchase.dto

data class GroupPurchaseCreateResponseDto(
    val id: Long,
    val title: String,
    val progressStatus: String,
    val imageUrl: String
)
