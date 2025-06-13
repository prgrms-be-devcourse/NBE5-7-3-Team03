package com.team573.gongguri.domain.review.service

import com.team573.gongguri.domain.grouppurchase.entity.GroupPurchase
import com.team573.gongguri.domain.grouppurchase.entity.ProgressStatus
import com.team573.gongguri.domain.grouppurchase.repository.GroupPurchaseRepository
import com.team573.gongguri.domain.member.entity.Member
import com.team573.gongguri.domain.member.repository.MemberRepository
import com.team573.gongguri.domain.review.entity.Review
import com.team573.gongguri.domain.review.mapper.toEntity
import com.team573.gongguri.domain.review.repository.ReviewRepository
import com.team573.gongguri.global.exception.CustomErrorCode
import com.team573.gongguri.global.exception.CustomException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReviewService(
	private val reviewRepository: ReviewRepository,
	private val memberRepository: MemberRepository,
	private val groupPurchaseRepository: GroupPurchaseRepository,
) {

	@Transactional
	fun addReview(groupPurchaseId: Long, memberId: Long, like: Boolean): Long {
		val member: Member =
			memberRepository.findByIdOrNull(memberId) ?: throw CustomException(CustomErrorCode.NOT_FOUND_MEMBER)
		val groupPurchase: GroupPurchase = groupPurchaseRepository.findByIdOrNull(groupPurchaseId)
			?: throw CustomException(CustomErrorCode.NOT_FOUND_GROUP_PURCHASE)

		if (groupPurchase.progressStatus != ProgressStatus.COMPLETED) {
			throw CustomException(CustomErrorCode.IS_NOT_COMPLETED)
		}

		val createdReview: Review = reviewRepository.save(toEntity(groupPurchase, member, like))

		groupPurchase.member.updateLikeCount(like)

		return createdReview.reviewId!!
	}
}
