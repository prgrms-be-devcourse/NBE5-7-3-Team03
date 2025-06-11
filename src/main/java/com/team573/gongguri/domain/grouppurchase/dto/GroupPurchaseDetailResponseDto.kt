package com.team573.gongguri.domain.grouppurchase.dto

data class GroupPurchaseDetailResponseDto(
    val id: Long,
    val title: String,
    val content: String,
    val price: Int,
    val maxParticipants: Int,
    val currentParticipants: Long,
    val bank: String,
    val account: String,
    val progressStatus: String,
    val imageUrl: String,
    val isParticipated: Boolean,
    val writerEmail: String,
    val writerNickname: String,
    val writerId: Long
)
