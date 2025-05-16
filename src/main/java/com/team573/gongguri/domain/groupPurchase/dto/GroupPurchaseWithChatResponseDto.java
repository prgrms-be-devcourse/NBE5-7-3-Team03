package com.team573.gongguri.domain.groupPurchase.dto;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record GroupPurchaseWithChatResponseDto(
    Long id,
    String title,
    Integer maxParticipants,
    Long participantCount,
    String progressStatus,
    String imageUrl,
    String chatMessage,
    LocalDateTime createAt
) {

}
