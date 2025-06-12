package com.team573.gongguri.domain.grouppurchase.dto

data class GroupPurchaseRequestDto(
    val title: String,
    val content: String,
    val price: Int,
    val maxParticipants: Int,
    val bank: String,
    val account: String,
    val progressStatus: String?,
    val imageUrl: String
)
