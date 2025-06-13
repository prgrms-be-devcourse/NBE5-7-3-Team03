package com.team573.gongguri.domain.grouppurchase.service

import com.team573.gongguri.domain.chat.service.ChatService
import com.team573.gongguri.domain.grouppurchase.dto.GroupPurchaseParticipantResponseDto
import com.team573.gongguri.domain.grouppurchase.entity.GroupPurchaseParticipant
import com.team573.gongguri.domain.grouppurchase.mapper.toDto
import com.team573.gongguri.domain.grouppurchase.repository.GroupPurchaseParticipantRepository
import com.team573.gongguri.domain.grouppurchase.repository.GroupPurchaseRepository
import com.team573.gongguri.global.exception.CustomErrorCode
import com.team573.gongguri.global.exception.CustomException
import lombok.RequiredArgsConstructor
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GroupPurchaseParticipantService (
    private val groupPurchaseParticipantRepository: GroupPurchaseParticipantRepository,
    private val groupPurchaseRepository: GroupPurchaseRepository,
    private val chatService: ChatService
){


    @Transactional
    fun cancelParticipation(groupPurchaseId: Long, participantId: Long, memberId: Long) {
        val participant = getParticipantToManaged(groupPurchaseId, participantId, memberId)

        // 이미 결제를 했다면 예외 처리
        if (participant.deposit) {
            throw CustomException(CustomErrorCode.CANNOT_CANCEL_PAID_PARTICIPANT)
        }

        chatService.deleteChatParticipation(groupPurchaseId, memberId)
        participant.cancelMember()
        groupPurchaseParticipantRepository.save(participant)
    }

    @Transactional
    fun confirmDeposit(groupPurchaseId: Long, participantId: Long, memberId: Long) {
        val participant = getParticipantToManaged(groupPurchaseId, participantId, memberId)

        participant.confirmDeposit()
        groupPurchaseParticipantRepository.save(participant)
    }

    @Transactional(readOnly = true)
    fun getParticipants(
        groupPurchaseId: Long,
        cursorParticipantId: Long?,
        deposit: Boolean?,
        memberId: Long,
        size: Int

    ): List<GroupPurchaseParticipantResponseDto> {
        val pageRequest = PageRequest.of(0, size)

        val participants = groupPurchaseParticipantRepository.findParticipantsByCursor(
            groupPurchaseId,
            cursorParticipantId,
            deposit,
            memberId,
            pageRequest
        )

        return participants.map { toDto(it) }
    }

    private fun getParticipantToManaged(
        groupPurchaseId: Long,
        participantId: Long,
        memberId: Long
    ): GroupPurchaseParticipant {
        // member가 공동 구매 관리자 인지 확인
        if (!groupPurchaseRepository.existsByGroupIdAndMember_MemberId(groupPurchaseId, memberId)) {
            throw CustomException(CustomErrorCode.UNAUTHORIZED_GROUP_PURCHASE_MANAGE)
        }

        // 관리하기 위한 참여자 조회
        return groupPurchaseParticipantRepository.findById(participantId)
            .orElseThrow { CustomException(CustomErrorCode.NOT_FOUND_PARTICIPANT) }
    }


}
