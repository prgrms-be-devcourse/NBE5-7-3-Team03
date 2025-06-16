package com.team573.gongguri.util

import com.team573.gongguri.domain.grouppurchase.entity.GroupPurchase
import com.team573.gongguri.domain.member.entity.Member
import com.team573.gongguri.domain.review.entity.Review

object ReviewUtil {
	fun createWithId(reviewId: Long, groupPurchase: GroupPurchase, member: Member, like: Boolean): Review {
		return Review(reviewId, groupPurchase, member, like)
	}

	fun create(groupPurchase: GroupPurchase, member: Member, like: Boolean): Review {
		return Review(null, groupPurchase, member, like)
	}
}