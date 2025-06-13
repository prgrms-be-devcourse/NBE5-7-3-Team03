package com.team573.gongguri.domain.grouppurchase.service

import com.team573.gongguri.domain.chat.entity.ChatRoom
import com.team573.gongguri.domain.chat.service.ChatService
import com.team573.gongguri.domain.grouppurchase.dto.*
import com.team573.gongguri.domain.grouppurchase.entity.*
import com.team573.gongguri.domain.grouppurchase.mapper.*
import com.team573.gongguri.domain.grouppurchase.repository.GroupPurchaseJpqlRepository
import com.team573.gongguri.domain.grouppurchase.repository.GroupPurchaseParticipantRepository
import com.team573.gongguri.domain.grouppurchase.repository.GroupPurchaseRepository
import com.team573.gongguri.domain.member.entity.Member
import com.team573.gongguri.domain.member.repository.MemberRepository
import com.team573.gongguri.domain.member.service.MemberService
import com.team573.gongguri.global.exception.CustomErrorCode
import com.team573.gongguri.global.exception.CustomException
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Slf4j
@RequiredArgsConstructor
class GroupPurchaseService(
    private val groupPurchaseRepository: GroupPurchaseRepository,
    private val memberRepository: MemberRepository,
    private val chatService: ChatService,
    private val groupPurchaseJpqlRepository: GroupPurchaseJpqlRepository,
    private val groupPurchaseParticipantRepository: GroupPurchaseParticipantRepository,
    private val memberService: MemberService
) {

    private fun getActiveGroupPurchase(id: Long): GroupPurchase {
        val groupPurchase = groupPurchaseRepository.findByGroupIdAndDeletedFalse(id)
            ?: throw CustomException(CustomErrorCode.NOT_FOUND_GROUP_PURCHASE)
        return groupPurchase
    }

    private fun registerParticipant(groupPurchase: GroupPurchase, member: Member) {
        try {
            val participant = toEntity(groupPurchase, member)
            groupPurchaseParticipantRepository.save(participant)
        } catch (e: Exception) {
            log.error("참여자 등록 실패", e)
            throw CustomException(CustomErrorCode.JOIN_FAILED)
        }
    }

    @Transactional
    fun add(dto: GroupPurchaseRequestDto, memberId: Long): GroupPurchaseCreateResponseDto {
        val writer = memberService.getMemberById(memberId)

        val univ = writer.univ
        val chatRoom: ChatRoom
        try {
            chatRoom = chatService.addChatRoom(writer.email)
        } catch (e: Exception) {
            log.error("채팅방 생성 실패", e)
            throw CustomException(CustomErrorCode.CREATE_FAILED_GROUP_PURCHASE)
        }

        val groupPurchase: GroupPurchase
        try {
            groupPurchase = toEntity(dto, writer, chatRoom, univ)
            groupPurchase.imageUrl = dto.imageUrl
        } catch (e: Exception) {
            log.error("공동구매 게시글 저장 실패", e)
            throw CustomException(CustomErrorCode.CREATE_FAILED_GROUP_PURCHASE)
        }

        val savedGroupPurchase = try {
            groupPurchaseRepository.save(groupPurchase)
        } catch (e: Exception) {
            log.error("공동구매 게시글 저장 실패", e)
            throw CustomException(CustomErrorCode.CREATE_FAILED_GROUP_PURCHASE)
        }

        registerParticipant(savedGroupPurchase, writer)
        return toCreateDto(savedGroupPurchase)
    }

    @Transactional(readOnly = true)
    operator fun get(id: Long, memberId: Long): GroupPurchaseDetailResponseDto {
        val groupPurchase = groupPurchaseRepository.findById(id)
            .orElseThrow { CustomException(CustomErrorCode.NOT_FOUND_GROUP_PURCHASE) }
        val currentParticipants = groupPurchaseParticipantRepository.countByGroupPurchaseAndParticipationStatus(
            groupPurchase,
            ParticipationStatus.JOINED
        )
        val isParticipated = groupPurchaseParticipantRepository.existsByGroupPurchase_GroupIdAndMember_MemberId(id, memberId)

        return toDetailDto(groupPurchase, currentParticipants, isParticipated)
    }

    @Transactional(readOnly = true)
    fun getAllByCursor(
        cursorId: Long?,
        statuses: List<ProgressStatus>,
        size: Int
    ): List<GroupPurchaseListResponseDto> {
        val groupPurchases: List<GroupPurchaseWithParticipantCountDto>
        try {
            groupPurchases =
                groupPurchaseJpqlRepository.findAllWithCursorAndParticipantCount(cursorId, statuses, size)
        } catch (e: Exception) {
            log.error("공동구매 목록 조회 실패: {}", e.message, e)
            throw CustomException(CustomErrorCode.FAILED_GROUP_PURCHASE_LIST)
        }
        return groupPurchases.map { toListDto(it) }
    }

    @Transactional
    fun update(id: Long, dto: GroupPurchaseRequestDto): GroupPurchaseUpdateResponseDto {
        val groupPurchase = getActiveGroupPurchase(id)
        try {
            groupPurchase.update(
                dto.title,
                dto.content,
                dto.price,
                dto.maxParticipants,
                dto.bank,
                dto.account,
                ProgressStatus.valueOf(dto.progressStatus!!.uppercase(Locale.getDefault()))
            )
            groupPurchase.imageUrl = dto.imageUrl
        } catch (e: Exception) {
            log.error("공동구매 수정 실패", e)
            throw CustomException(CustomErrorCode.UPDATE_FAILED_GROUP_PURCHASE)
        }
        return toUpdateDto(groupPurchase)
    }

    @Transactional
    fun delete(id: Long, memberId: Long) {
        val groupPurchase = getActiveGroupPurchase(id)
        if (groupPurchase.member.memberId != memberId) {
            throw CustomException(CustomErrorCode.UNAUTHORIZED_GROUP_PURCHASE_MANAGE)
        }

        if (groupPurchase.progressStatus != ProgressStatus.COMPLETED) {
            val hasDepositedParticipants = groupPurchaseParticipantRepository
                .existsByGroupPurchase_GroupIdAndDepositIsTrue(id)
            if (hasDepositedParticipants) {
                throw CustomException(CustomErrorCode.DELETE_FAILED_WITH_DEPOSITED_PARTICIPANTS)
            }
        }
        groupPurchase.markAsDeleted()
    }

    @Transactional
    fun join(groupId: Long, memberId: Long) {
        val member = memberService.getMemberById(memberId)

        val groupPurchase = getActiveGroupPurchase(groupId)

        val currentCount = countParticipantsByStatus(groupPurchase, ParticipationStatus.JOINED).toInt()
        if (currentCount >= groupPurchase.maxParticipants) {
            throw CustomException(CustomErrorCode.PARTICIPANT_LIMIT_REACHED)
        }

        val alreadyJoined = groupPurchaseParticipantRepository.existsByGroupPurchase_GroupIdAndMember_MemberId(groupId, memberId)
        if (alreadyJoined) {
            throw CustomException(CustomErrorCode.ALREADY_JOINED)
        }

        registerParticipant(groupPurchase, member)

        try {
            val chatRoomId = groupPurchase.chatRoom.chatRoomId
                ?: throw CustomException(CustomErrorCode.NOT_FOUND_CHATROOM)
            chatService.addChatParticipation(chatRoomId, member.email)
        } catch (e: Exception) {
            log.error("채팅방 참여 실패", e)
            throw CustomException(CustomErrorCode.CHAT_JOIN_FALED)
        }
        val afterJoinCount = currentCount + 1
        if (afterJoinCount >= groupPurchase.maxParticipants) {
            groupPurchase.progressStatus = ProgressStatus.CLOSED
        }
    }

    @Transactional(readOnly = true)
    fun getWithMessage(
        size: Int,
        cursorId: Long?,
        purchaseFilter: PurchaseFilter,
        memberId: Long
    ): List<GroupPurchaseWithChatResponseDto> {
        val statuses = purchaseFilter.toStatuses()

        val pageable = PageRequest.of(0, size)

        val groupPurchaseParticipants =
            groupPurchaseParticipantRepository.findByMemberWithCursor(cursorId, memberId, statuses, pageable)

        val groupPurchases = groupPurchaseParticipants.map { it.groupPurchase }

        val firstMessages = getFirstMessages(groupPurchases)

        return groupPurchaseParticipants.map {
            toDtoWithMessage(
                it,
                countParticipantsByStatus(it.groupPurchase, ParticipationStatus.JOINED),
                firstMessages
            )
        }
    }

    // 조회한 공동 구매 채팅 메시지 조회
    private fun getFirstMessages(groupPurchases: List<GroupPurchase>): Map<Long, String> =
        chatService.getFirstMessageMap(groupPurchases.map { it.chatRoom.chatRoomId
            ?: throw CustomException(CustomErrorCode.NOT_FOUND_CHATROOM)
        })

    // ParticipationStatus 로 해당 공동 구매 참여자 수 조회
    private fun countParticipantsByStatus(groupPurchase: GroupPurchase, status: ParticipationStatus): Long =
        groupPurchaseParticipantRepository.countByGroupPurchaseAndParticipationStatus(groupPurchase, status)


    @Transactional(readOnly = true)
    fun getSimpleInfo(groupPurchaseId: Long): GroupPurchaseSimpleResponseDto {
        val groupPurchase = groupPurchaseRepository.findById(groupPurchaseId)
            .orElseThrow { CustomException(CustomErrorCode.NOT_FOUND_GROUP_PURCHASE) }

        val participantCount = groupPurchaseParticipantRepository.countByGroupPurchaseAndParticipationStatus(
            groupPurchase,
            ParticipationStatus.JOINED
        )

        return toDtoWithCount(groupPurchase, participantCount)
    }

    //특정 멤버가 작성한 공동구매글 조회
    fun findCreatedPurchases(memberId: Long, purchaseFilter: PurchaseFilter): List<GroupPurchaseListResponseDto> {
        memberRepository.findById(memberId).orElseThrow { CustomException(CustomErrorCode.NOT_FOUND_MEMBER) }

        val purchases: List<GroupPurchase>
        val statuses = purchaseFilter.toStatuses()

        purchases = if (statuses.size == ProgressStatus.entries.size) {
            groupPurchaseRepository.findByMember_MemberId(memberId)
        } else {
            groupPurchaseRepository.findByMember_MemberIdAndProgressStatusIn(memberId, statuses)
        }

        return purchases.map {
            val count = countParticipantsByStatus(it, ParticipationStatus.JOINED)
            toListDto(it, count)
        }
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(GroupPurchaseService::class.java)
    }
}
