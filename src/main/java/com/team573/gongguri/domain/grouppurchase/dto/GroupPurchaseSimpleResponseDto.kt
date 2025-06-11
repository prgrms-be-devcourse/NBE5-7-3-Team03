package com.team573.gongguri.domain.grouppurchase.dto;

import com.team573.gongguri.domain.grouppurchase.entity.ProgressStatus;
import lombok.Builder;

@Builder
public record GroupPurchaseSimpleResponseDto(
    Long id,
    String title,
    int maxParticipants,
    Long participantCount,
    ProgressStatus progressStatus,
    String imageUrl,
    int price
) {
}
