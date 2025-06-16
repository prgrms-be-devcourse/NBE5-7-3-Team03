package com.team573.gongguri.domain.review.mapper

import com.team573.gongguri.domain.grouppurchase.entity.GroupPurchase
import com.team573.gongguri.domain.member.entity.Member
import com.team573.gongguri.domain.review.entity.Review

fun toEntity(groupPurchase: GroupPurchase, member: Member, like: Boolean): Review {
	return Review(
		groupPurchase = groupPurchase,
		member = member,
		liked = like
	)
}
