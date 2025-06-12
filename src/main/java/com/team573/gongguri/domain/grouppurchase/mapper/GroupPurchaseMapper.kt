package com.team573.gongguri.domain.grouppurchase.mapper

import com.team573.gongguri.domain.chat.entity.ChatRoom
import com.team573.gongguri.domain.grouppurchase.dto.*
import com.team573.gongguri.domain.grouppurchase.entity.GroupPurchase
import com.team573.gongguri.domain.grouppurchase.entity.GroupPurchaseParticipant
import com.team573.gongguri.domain.grouppurchase.entity.ProgressStatus
import com.team573.gongguri.domain.member.entity.Member
import com.team573.gongguri.domain.member.entity.Univ

fun toEntity(dto: GroupPurchaseRequestDto, writer: Member, chatRoom: ChatRoom, univ: Univ): GroupPurchase {
    return GroupPurchase(
        member = writer,
        chatRoom = chatRoom,
        univ = univ,
        title = dto.title,
        content = dto.content,
        price = dto.price,
        maxParticipants = dto.maxParticipants,
        bank = dto.bank,
        account = dto.account,
        imageUrl = dto.imageUrl,
        progressStatus = ProgressStatus.RECRUITING
    )
}

fun toCreateDto(entity: GroupPurchase): GroupPurchaseCreateResponseDto {
    return GroupPurchaseCreateResponseDto(
        id = entity.groupId!!,
        title = entity.title,
        progressStatus = entity.progressStatus.toString(),
        imageUrl = entity.imageUrl
    )
}

fun toUpdateDto(entity: GroupPurchase): GroupPurchaseUpdateResponseDto {
    return GroupPurchaseUpdateResponseDto(
        id = entity.groupId!!,
        title = entity.title,
        content = entity.content,
        price = entity.price,
        maxParticipants = entity.maxParticipants,
        progressStatus = entity.progressStatus.toString(),
        imageUrl = entity.imageUrl
    )
}

fun toDetailDto(
    entity: GroupPurchase,
    currentParticipants: Long,
    isParticipated: Boolean
): GroupPurchaseDetailResponseDto {
    return GroupPurchaseDetailResponseDto(
        id = entity.groupId!!,
        title = entity.title,
        content = entity.content,
        price = entity.price,
        maxParticipants = entity.maxParticipants,
        currentParticipants = currentParticipants,
        bank = entity.bank,
        account = entity.account,
        progressStatus = entity.progressStatus.toString(),
        imageUrl = entity.imageUrl,
        isParticipated = isParticipated,
        writerEmail = entity.member.email,
        writerNickname = entity.member.nickname,
        writerId = entity.member.memberId!!
    )
}

fun toDtoWithMessage(
    groupPurchaseParticipant: GroupPurchaseParticipant,
    participantCount: Long,
    firstMessages: Map<Long, String>
): GroupPurchaseWithChatResponseDto {
    val purchase = groupPurchaseParticipant.groupPurchase
    return GroupPurchaseWithChatResponseDto(
        id = purchase.groupId!!,
        participantId = groupPurchaseParticipant.groupParticipantId!!,
        title = purchase.title,
        maxParticipants = purchase.maxParticipants,
        progressStatus = purchase.progressStatus.toString(),
        imageUrl = purchase.imageUrl,
        chatMessage = firstMessages[purchase.chatRoom.chatRoomId],
        participantCount = participantCount,
        createAt = groupPurchaseParticipant.createdAt
    )
}

fun toListDto(dto: GroupPurchaseWithParticipantCountDto): GroupPurchaseListResponseDto {
    return GroupPurchaseListResponseDto(
        id = dto.groupId,
        title = dto.title,
        price = dto.price,
        maxParticipants = dto.maxParticipants,
        currentParticipants = dto.participantCount,
        progressStatus = dto.progressStatus.toString(),
        imageUrl = dto.imageUrl
    )
}

fun toDtoWithCount(groupPurchase: GroupPurchase, participantCount: Long): GroupPurchaseSimpleResponseDto {
    return GroupPurchaseSimpleResponseDto(
        id = groupPurchase.groupId!!,
        title = groupPurchase.title,
        maxParticipants = groupPurchase.maxParticipants,
        participantCount = participantCount,
        progressStatus = groupPurchase.progressStatus,
        imageUrl = groupPurchase.imageUrl,
        price = groupPurchase.price
    )
}

fun toListDto(purchase: GroupPurchase, currentParticipants: Long): GroupPurchaseListResponseDto {
    return GroupPurchaseListResponseDto(
        id = purchase.groupId!!,
        title = purchase.title,
        price = purchase.price,
        progressStatus = purchase.progressStatus.name,
        currentParticipants = currentParticipants,
        maxParticipants = purchase.maxParticipants,
        imageUrl = purchase.imageUrl
    )
}

fun toDtoWithReviewed(
    groupPurchase: GroupPurchase,
    participantCount: Long,
    isReviewed: Boolean
): GroupPurchaseWithReviewedResponseDto {
    return GroupPurchaseWithReviewedResponseDto(
        id = groupPurchase.groupId!!,
        title = groupPurchase.title,
        maxParticipants = groupPurchase.maxParticipants,
        participantCount = participantCount,
        isReviewed = isReviewed,
        imageUrl = groupPurchase.imageUrl,
        price = groupPurchase.price
    )
}