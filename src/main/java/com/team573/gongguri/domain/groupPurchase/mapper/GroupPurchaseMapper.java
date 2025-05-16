package com.team573.gongguri.domain.groupPurchase.mapper;

import com.team573.gongguri.domain.chat.entity.ChatRoom;
import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseWithChatResponseDto;
import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseRequestDto;
import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseResponseDto;
import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseWithParticipantCountDto;
import com.team573.gongguri.domain.groupPurchase.entity.GroupPurchase;
import com.team573.gongguri.domain.groupPurchase.entity.ProgressStatus;
import com.team573.gongguri.domain.member.entity.Member;
import com.team573.gongguri.domain.member.entity.Univ;
import java.util.Map;

public class GroupPurchaseMapper {
    public static GroupPurchase toEntity(GroupPurchaseRequestDto dto, Member writer, ChatRoom chatRoom, Univ univ) {
        return GroupPurchase.builder()
                .member(writer)
                .chatRoom(chatRoom)
                .univ(univ)
                .title(dto.title())
                .content(dto.content())
                .price(dto.price())
                .maxParticipants(dto.maxParticipants())
                .bank(dto.bank())
                .account(dto.account())
                .progressStatus(ProgressStatus.RECRUITING)
                .imageUrl(dto.imageUrl()) // 추가!
                .build(); // imageUrl은 add/update 시 service에서 set
    }

    public static GroupPurchaseResponseDto toDto(GroupPurchase entity) {
        return new GroupPurchaseResponseDto(
                entity.getGroupId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getPrice(),
                entity.getMaxParticipants(),
                entity.getBank(),
                entity.getAccount(),
                entity.getProgressStatus().toString(),
                entity.getImageUrl()
        );
    }

    public static GroupPurchaseWithChatResponseDto toWithMessageResponseDto(
        GroupPurchaseWithParticipantCountDto dto,
        Map<Long, String> firstMessages
    ) {
        return GroupPurchaseWithChatResponseDto.builder()
            .id(dto.groupId())
            .title(dto.title())
            .maxParticipants(dto.maxParticipants())
            .progressStatus(dto.progressStatus().toString())
            .imageUrl(dto.imageUrl())
            .chatMessage(firstMessages.get(dto.chatRoomId()))
            .participantCount(dto.participantCount())
            .createAt(dto.createdAt())
            .build();
    }
}
