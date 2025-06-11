package com.team573.gongguri.domain.grouppurchase.dto;

import lombok.Builder;

@Builder
public record GroupPurchaseParticipantResponseDto(
    Long groupParticipantId,
    String nickname,
    Boolean deposit
) {

}
