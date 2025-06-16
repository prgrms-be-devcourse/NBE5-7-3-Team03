package com.team573.gongguri.domain.review.service

import com.team573.gongguri.domain.grouppurchase.entity.ProgressStatus
import com.team573.gongguri.domain.grouppurchase.repository.GroupPurchaseRepository
import com.team573.gongguri.domain.member.repository.MemberRepository
import com.team573.gongguri.domain.review.repository.ReviewRepository
import com.team573.gongguri.global.exception.CustomErrorCode
import com.team573.gongguri.global.exception.CustomException
import com.team573.gongguri.util.ChatRoomUtil
import com.team573.gongguri.util.GroupPurchaseUtil
import com.team573.gongguri.util.MemberUtil
import com.team573.gongguri.util.ReviewUtil
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull

class ReviewServiceTests {
	val reviewRepository = mockk<ReviewRepository>()
	val memberRepository = mockk<MemberRepository>()
	val groupPurchaseRepository = mockk<GroupPurchaseRepository>()
	val service = ReviewService(reviewRepository, memberRepository, groupPurchaseRepository)

	@Nested
	inner class AddReviewTests {
		@Test
		fun `회원과 공동 구매가 존재하고 완료 상태이면 리뷰를 추가한다`() {
			// given
			val reviewId = 1L
			val groupPurchaseId = 1L
			val memberId = 1L
			val like = true
			val member = MemberUtil.createWithId(memberId)
			val groupPurchase = GroupPurchaseUtil.createWithId(groupPurchaseId, member, ChatRoomUtil.createWithId(1), ProgressStatus.COMPLETED)
			val savedReview = ReviewUtil.createWithId(reviewId, groupPurchase, member, like)
			val createdReview = ReviewUtil.create(groupPurchase, member, like)

			every { memberRepository.findByIdOrNull(memberId) } returns member
			every { groupPurchaseRepository.findByIdOrNull(groupPurchaseId) } returns groupPurchase
			every { reviewRepository.save(createdReview) } returns savedReview

			// when
			val result = service.addReview(groupPurchaseId, memberId, like)

			// then
			result shouldBe reviewId
		}

		@Test
		fun `회원이 존재하지 않으면 예외가 발생한다`() {
			// given
			val groupPurchaseId = 1L
			val memberId = 1L
			val like = true

			every { memberRepository.findByIdOrNull(memberId) } returns null

			// when
			val exception = assertThrows<CustomException> { service.addReview(groupPurchaseId, memberId, like) }

			// then
			exception.getCustomErrorCode() shouldBe CustomErrorCode.NOT_FOUND_MEMBER
		}

		@Test
		fun `공동구매가 존재하지 않으면 예외가 발생한다`() {
			// given
			val groupPurchaseId = 1L
			val memberId = 1L
			val like = true
			val member = MemberUtil.createWithId(memberId)

			every { memberRepository.findByIdOrNull(memberId) } returns member
			every { groupPurchaseRepository.findByIdOrNull(groupPurchaseId) } returns null

			// when
			val exception = assertThrows<CustomException> { service.addReview(groupPurchaseId, memberId, like) }

			// then
			exception.getCustomErrorCode() shouldBe CustomErrorCode.NOT_FOUND_GROUP_PURCHASE
		}

		@Test
		fun `공동 구매가 완료 상태가 아니면 예외가 발생한다`() {
			// given
			val reviewId = 1L
			val groupPurchaseId = 1L
			val memberId = 1L
			val like = true
			val member = MemberUtil.createWithId(memberId)
			val groupPurchase = GroupPurchaseUtil.createWithId(groupPurchaseId, member, ChatRoomUtil.createWithId(1), ProgressStatus.RECRUITING)

			every { memberRepository.findByIdOrNull(memberId) } returns member
			every { groupPurchaseRepository.findByIdOrNull(groupPurchaseId) } returns groupPurchase

			// when
			val exception = assertThrows<CustomException> { service.addReview(groupPurchaseId, memberId, like) }

			// then
			exception.getCustomErrorCode() shouldBe CustomErrorCode.IS_NOT_COMPLETED
		}
	}
}