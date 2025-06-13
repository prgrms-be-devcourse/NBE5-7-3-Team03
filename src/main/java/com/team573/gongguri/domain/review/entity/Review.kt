package com.team573.gongguri.domain.review.entity

import com.team573.gongguri.domain.grouppurchase.entity.GroupPurchase
import com.team573.gongguri.domain.member.entity.Member
import com.team573.gongguri.global.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "review")
class Review (
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	val reviewId: Long? = null,

	@JoinColumn(name = "group_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	var groupPurchase: GroupPurchase,

	@JoinColumn(name = "member_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	var member: Member,

	@Column(nullable = false)
	var liked: Boolean
) : BaseEntity()
