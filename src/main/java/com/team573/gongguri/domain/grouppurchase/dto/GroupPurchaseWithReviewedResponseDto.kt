package com.team573.gongguri.domain.grouppurchase.dto

data class GroupPurchaseWithReviewedResponseDto(
    val id: Long,
    val title: String,
    val maxParticipants: Int,
    val participantCount: Long,
    val isReviewed: Boolean,
    val imageUrl: String,
    val price: Int
)
