package com.team573.gongguri.domain.review.repository

import com.team573.gongguri.domain.review.entity.Review
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReviewRepository : JpaRepository<Review, Long> {
	fun existsByGroupPurchase_groupIdAndMember_memberId(groupId: Long?, memberId: Long): Boolean
}
