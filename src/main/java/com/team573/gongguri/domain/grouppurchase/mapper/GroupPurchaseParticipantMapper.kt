package com.team573.gongguri.domain.grouppurchase.mapper;

import com.team573.gongguri.domain.grouppurchase.dto.GroupPurchaseParticipantResponseDto;
import com.team573.gongguri.domain.grouppurchase.entity.GroupPurchase;
import com.team573.gongguri.domain.grouppurchase.entity.GroupPurchaseParticipant;
import com.team573.gongguri.domain.grouppurchase.entity.ParticipationStatus;
import com.team573.gongguri.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GroupPurchaseParticipantMapper {
       public static GroupPurchaseParticipant toEntity(GroupPurchase groupPurchase, Member member) {
        return GroupPurchaseParticipant.builder()
                .groupPurchase(groupPurchase)
                .member(member)
                .participationStatus(ParticipationStatus.JOINED)
                .build();
    }

    public static GroupPurchaseParticipantResponseDto toDto(GroupPurchaseParticipant groupPurchaseParticipant) {
        return GroupPurchaseParticipantResponseDto.builder()
            .groupParticipantId(groupPurchaseParticipant.getGroupParticipantId())
            .deposit(groupPurchaseParticipant.getDeposit())
            .nickname(groupPurchaseParticipant.getMember().getNickname())
            .build();
    }
}
