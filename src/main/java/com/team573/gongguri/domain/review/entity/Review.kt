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
) : BaseEntity() {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as Review

		if (reviewId != other.reviewId) return false
		if (liked != other.liked) return false
		if (groupPurchase != other.groupPurchase) return false
		if (member != other.member) return false

		return true
	}

	override fun hashCode(): Int {
		var result = reviewId?.hashCode() ?: 0
		result = 31 * result + liked.hashCode()
		result = 31 * result + groupPurchase.hashCode()
		result = 31 * result + member.hashCode()
		return result
	}
}
