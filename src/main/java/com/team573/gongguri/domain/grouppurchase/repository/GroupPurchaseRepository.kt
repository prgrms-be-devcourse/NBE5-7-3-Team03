package com.team573.gongguri.domain.grouppurchase.repository

import com.team573.gongguri.domain.grouppurchase.entity.GroupPurchase
import com.team573.gongguri.domain.grouppurchase.entity.ProgressStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface GroupPurchaseRepository : JpaRepository<GroupPurchase, Long> {
    fun findByMember_MemberId(memberId: Long): List<GroupPurchase>

    fun findByMember_MemberIdAndProgressStatusIn(
        memberId: Long,
        recruiting: List<ProgressStatus>
    ): List<GroupPurchase>

    // softDelete 단건 조회
    fun findByGroupIdAndDeletedFalse(groupId: Long): GroupPurchase?

    fun existsByGroupIdAndMember_MemberId(groupId: Long, memberId: Long): Boolean
}
