package com.team573.gongguri.domain.grouppurchase.dto;

import com.team573.gongguri.domain.grouppurchase.entity.ProgressStatus;
import java.time.LocalDateTime;

public record GroupPurchaseWithParticipantCountDto(
    Long groupId,
    String title,
    String content,
    int price,
    int maxParticipants,
    ProgressStatus progressStatus,
    LocalDateTime createdAt,
    Long chatRoomId,
    Long participantCount,
    String imageUrl
) {
}
