package com.team573.gongguri.domain.grouppurchase.mapper;

import com.team573.gongguri.domain.chat.entity.ChatRoom;
import com.team573.gongguri.domain.grouppurchase.dto.*;
import com.team573.gongguri.domain.grouppurchase.entity.GroupPurchase;
import com.team573.gongguri.domain.grouppurchase.entity.ProgressStatus;
import com.team573.gongguri.domain.grouppurchase.entity.GroupPurchaseParticipant;
import com.team573.gongguri.domain.member.entity.Member;
import com.team573.gongguri.domain.member.entity.Univ;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
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
                .imageUrl(dto.imageUrl()) // 추가!
                .progressStatus(ProgressStatus.RECRUITING)
                .build();
    }

    public static GroupPurchaseCreateResponseDto toCreateDto(GroupPurchase entity) {
        return GroupPurchaseCreateResponseDto.builder()
                .id(entity.getGroupId())
                .title(entity.getTitle())
                .progressStatus(entity.getProgressStatus().toString())
                .imageUrl(entity.getImageUrl())
                .build();
    }

    public static GroupPurchaseUpdateResponseDto toUpdateDto(GroupPurchase entity) {
        return GroupPurchaseUpdateResponseDto.builder()
                .id(entity.getGroupId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .price(entity.getPrice())
                .maxParticipants(entity.getMaxParticipants())
                .progressStatus(entity.getProgressStatus().toString())
                .imageUrl(entity.getImageUrl())
                .build();
    }
    public static GroupPurchaseDetailResponseDto toDetailDto(GroupPurchase entity, Long currentParticipants, boolean isParticipated) {
        return GroupPurchaseDetailResponseDto.builder()
                .id(entity.getGroupId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .price(entity.getPrice())
                .maxParticipants(entity.getMaxParticipants())
                .currentParticipants(currentParticipants)
                .bank(entity.getBank())
                .account(entity.getAccount())
                .progressStatus(entity.getProgressStatus().toString())
                .imageUrl(entity.getImageUrl())
                .isParticipated(isParticipated)
                .writerEmail(entity.getMember().getEmail())
                .writerNickname(entity.getMember().getNickname())
                .writerId(entity.getMember().getMemberId())
                .build();
    }

    public static GroupPurchaseWithChatResponseDto toDtoWithMessage(
        GroupPurchaseParticipant groupPurchaseParticipant,
        Long participantCount,
        Map<Long, String> firstMessages
    ) {
        return GroupPurchaseWithChatResponseDto.builder()
            .id(groupPurchaseParticipant.getGroupPurchase().getGroupId())
            .participantId(groupPurchaseParticipant.getGroupParticipantId())
            .title(groupPurchaseParticipant.getGroupPurchase().getTitle())
            .maxParticipants(groupPurchaseParticipant.getGroupPurchase().getMaxParticipants())
            .progressStatus(groupPurchaseParticipant.getGroupPurchase().getProgressStatus().toString())
            .imageUrl(groupPurchaseParticipant.getGroupPurchase().getImageUrl())
            .chatMessage(firstMessages.get(groupPurchaseParticipant.getGroupPurchase().getChatRoom().getChatRoomId()))
            .participantCount(participantCount)
            .createAt(groupPurchaseParticipant.getCreatedAt())
            .build();
    }

    public static GroupPurchaseListResponseDto toListDto(GroupPurchaseWithParticipantCountDto dto) {
        return GroupPurchaseListResponseDto.builder()
                .id(dto.groupId())
                .title(dto.title())
                .price(dto.price())
                .maxParticipants(dto.maxParticipants())
                .currentParticipants(dto.participantCount())
                .progressStatus(dto.progressStatus().toString())
                .imageUrl(dto.imageUrl())
                .build();
    }

    public static GroupPurchaseSimpleResponseDto toDtoWithCount(GroupPurchase groupPurchase, Long participantCount) {
        return GroupPurchaseSimpleResponseDto.builder()
            .id(groupPurchase.getGroupId())
            .title(groupPurchase.getTitle())
            .maxParticipants(groupPurchase.getMaxParticipants())
            .participantCount(participantCount)
            .progressStatus(groupPurchase.getProgressStatus())
            .imageUrl(groupPurchase.getImageUrl())
            .price(groupPurchase.getPrice())
            .build();
    }

    public static GroupPurchaseListResponseDto toListDto(GroupPurchase purchase, Long currentParticipants) {
        return GroupPurchaseListResponseDto.builder()
                .id(purchase.getGroupId())
                .title(purchase.getTitle())
                .price(purchase.getPrice())
                .progressStatus(purchase.getProgressStatus().name())
                .currentParticipants(currentParticipants)
                .maxParticipants(purchase.getMaxParticipants())
                .imageUrl(purchase.getImageUrl()) // 없으면 null 처리
                .build();
    }

    public static GroupPurchaseWithReviewedResponseDto toDtoWithReviewed(
        GroupPurchase groupPurchase,
        Long participantCount,
        Boolean isReviewed
    ) {
        return GroupPurchaseWithReviewedResponseDto.builder()
            .id(groupPurchase.getGroupId())
            .title(groupPurchase.getTitle())
            .maxParticipants(groupPurchase.getMaxParticipants())
            .participantCount(participantCount)
            .isReviewed(isReviewed)
            .imageUrl(groupPurchase.getImageUrl())
            .price(groupPurchase.getPrice())
            .build();
    }
}

