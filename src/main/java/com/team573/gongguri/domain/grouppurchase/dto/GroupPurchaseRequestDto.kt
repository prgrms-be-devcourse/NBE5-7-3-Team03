package com.team573.gongguri.domain.grouppurchase.dto;

public record GroupPurchaseRequestDto(
        String title,
        String content,
        int price,
        int maxParticipants,
        String bank,
        String account,
        String progressStatus,
        String imageUrl
) {}
