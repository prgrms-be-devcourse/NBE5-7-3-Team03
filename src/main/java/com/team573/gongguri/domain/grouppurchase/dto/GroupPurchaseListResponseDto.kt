package com.team573.gongguri.domain.grouppurchase.dto;

import lombok.Builder;

@Builder
public record GroupPurchaseListResponseDto (
        Long id,
        String title,
        int price,
        int maxParticipants,
        Long currentParticipants,
        String progressStatus,
        String imageUrl
) {}
