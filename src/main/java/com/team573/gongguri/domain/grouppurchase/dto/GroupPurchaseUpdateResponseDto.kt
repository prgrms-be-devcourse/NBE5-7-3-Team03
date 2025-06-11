package com.team573.gongguri.domain.grouppurchase.dto;

import lombok.Builder;

@Builder
public record GroupPurchaseUpdateResponseDto(
        Long id,
        String title,
        String content,
        int price,
        int maxParticipants,
        String progressStatus,
        String imageUrl
) {}
