package com.team573.gongguri.domain.groupPurchase.dto;

import lombok.Builder;

@Builder
public record GroupPurchaseWithReviewedResponseDto(
    Long id,
    String title,
    int maxParticipants,
    Long participantCount,
    Boolean isReviewed,
    String imageUrl,
    int price
) {

}
