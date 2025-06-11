package com.team573.gongguri.domain.chat.dto;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ChatMessageResponseDto(
    String messageId,
    String nickname,
    String content,
    LocalDateTime createdAt
) {

}
