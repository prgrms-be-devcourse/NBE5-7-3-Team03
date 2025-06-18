package com.team573.gongguri.domain.myPage.service

import com.team573.gongguri.domain.grouppurchase.dto.GroupPurchaseWithReviewedResponseDto
import com.team573.gongguri.domain.grouppurchase.entity.GroupPurchase
import com.team573.gongguri.domain.grouppurchase.entity.GroupPurchaseParticipant
import com.team573.gongguri.domain.grouppurchase.entity.ParticipationStatus
import com.team573.gongguri.domain.grouppurchase.entity.ProgressStatus
import com.team573.gongguri.domain.grouppurchase.repository.GroupPurchaseParticipantRepository
import com.team573.gongguri.domain.review.repository.ReviewRepository
import com.team573.gongguri.domain.grouppurchase.mapper.toDtoWithReviewed
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class MyPageServiceTests {

    private val groupPurchaseParticipantRepository = mockk<GroupPurchaseParticipantRepository>()
    private val reviewRepository = mockk<ReviewRepository>()

    private val myPageService = MyPageService(
        groupPurchaseParticipantRepository,
        reviewRepository
    )

    @Test
    @DisplayName("내가 참여한 완료된 공구글을 조회하면 DTO 리스트를 반환한다")
    fun `findMyParticipatedPurchases 정상 케이스`() {
        // given
        val memberId = 1L
        val groupId = 100L
        val participantCount = 3L
        val hasReviewed = true

        val groupPurchase = mockk<GroupPurchase>()
        every { groupPurchase.groupId } returns groupId

        val participant = mockk<GroupPurchaseParticipant>()
        every { participant.groupPurchase } returns groupPurchase

        val expectedDto = mockk<GroupPurchaseWithReviewedResponseDto>()

        every {
            groupPurchaseParticipantRepository.findByMember_MemberIdAndGroupPurchase_ProgressStatus(
                memberId, ProgressStatus.COMPLETED
            )
        } returns listOf(participant)

        every {
            groupPurchaseParticipantRepository.countByGroupPurchaseAndParticipationStatus(
                groupPurchase, ParticipationStatus.JOINED
            )
        } returns participantCount

        every {
            reviewRepository.existsByGroupPurchase_groupIdAndMember_memberId(
                groupId, memberId
            )
        } returns hasReviewed

        // top-level 함수 mocking
        mockkStatic("com.team573.gongguri.domain.grouppurchase.mapper.GroupPurchaseMapperKt")
        every {
            toDtoWithReviewed(groupPurchase, participantCount, hasReviewed)
        } returns expectedDto

        // when
        val result = myPageService.findMyParticipatedPurchases(memberId)

        // then
        result shouldHaveSize 1
        result[0] shouldBe expectedDto


        unmockkStatic("com.team573.gongguri.domain.grouppurchase.mapper.GroupPurchaseMapperKt")
    }
}