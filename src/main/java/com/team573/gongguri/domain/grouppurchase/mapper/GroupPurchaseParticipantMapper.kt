package com.team573.gongguri.domain.grouppurchase.mapper

import com.team573.gongguri.domain.grouppurchase.dto.GroupPurchaseParticipantResponseDto
import com.team573.gongguri.domain.grouppurchase.entity.GroupPurchase
import com.team573.gongguri.domain.grouppurchase.entity.GroupPurchaseParticipant
import com.team573.gongguri.domain.grouppurchase.entity.ParticipationStatus
import com.team573.gongguri.domain.member.entity.Member

fun toEntity(groupPurchase: GroupPurchase, member: Member): GroupPurchaseParticipant {
    return GroupPurchaseParticipant(
        groupPurchase = groupPurchase,
        member = member,
        participationStatus = ParticipationStatus.JOINED
    )
}

fun toDto(groupPurchaseParticipant: GroupPurchaseParticipant): GroupPurchaseParticipantResponseDto {
    return GroupPurchaseParticipantResponseDto(
        groupParticipantId = groupPurchaseParticipant.groupParticipantId!!,
        deposit = groupPurchaseParticipant.deposit,
        nickname = groupPurchaseParticipant.member.nickname
    )
}
