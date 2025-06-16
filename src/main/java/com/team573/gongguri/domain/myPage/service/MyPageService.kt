package com.team573.gongguri.domain.myPage.service

import com.team573.gongguri.domain.grouppurchase.dto.GroupPurchaseWithReviewedResponseDto
import com.team573.gongguri.domain.grouppurchase.entity.GroupPurchase
import com.team573.gongguri.domain.grouppurchase.entity.GroupPurchaseParticipant
import com.team573.gongguri.domain.grouppurchase.entity.ParticipationStatus
import com.team573.gongguri.domain.grouppurchase.entity.ProgressStatus
import com.team573.gongguri.domain.grouppurchase.mapper.toDtoWithReviewed
import com.team573.gongguri.domain.grouppurchase.repository.GroupPurchaseParticipantRepository
import com.team573.gongguri.domain.review.repository.ReviewRepository
import org.springframework.stereotype.Service

@Service
class MyPageService(
    private val groupPurchaseParticipantRepository: GroupPurchaseParticipantRepository,
    private val reviewRepository: ReviewRepository
) {
    // 내가 참여한 공구글
    fun findMyParticipatedPurchases(memberId: Long): List<GroupPurchaseWithReviewedResponseDto> {
        // '본인이 참여했으며, 연관된 공구가 완료된' 공구 참여자 entity 조회
        val participants = groupPurchaseParticipantRepository.findByMember_MemberIdAndGroupPurchase_ProgressStatus(
                memberId,
                ProgressStatus.COMPLETED
        )

        return participants
            .map { it.groupPurchase }
            .map { toWithReviewedDto(it, memberId) }
    }

    private fun toWithReviewedDto(groupPurchase: GroupPurchase, memberId: Long): GroupPurchaseWithReviewedResponseDto {
        val participantCount = groupPurchaseParticipantRepository.countByGroupPurchaseAndParticipationStatus(
            groupPurchase,
            ParticipationStatus.JOINED
        )

        val isReviewed = reviewRepository.existsByGroupPurchase_groupIdAndMember_memberId(
            groupPurchase.groupId,
            memberId
        )

        return toDtoWithReviewed(groupPurchase, participantCount, isReviewed)
    }
}
