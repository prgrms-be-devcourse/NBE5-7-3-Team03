package com.team573.gongguri.domain.grouppurchase.repository

import com.team573.gongguri.domain.grouppurchase.entity.GroupPurchase
import com.team573.gongguri.domain.grouppurchase.entity.GroupPurchaseParticipant
import com.team573.gongguri.domain.grouppurchase.entity.ParticipationStatus
import com.team573.gongguri.domain.grouppurchase.entity.ProgressStatus
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface GroupPurchaseParticipantRepository : JpaRepository<GroupPurchaseParticipant, Long> {
    fun existsByGroupPurchase_GroupIdAndMember_MemberId(
        groupPurchaseGroupId: Long,
        memberId: Long
    ): Boolean

    fun existsByGroupPurchase_GroupIdAndDepositIsTrue(groupId: Long): Boolean

    fun findByMember_MemberIdAndGroupPurchase_ProgressStatus(
        memberId: Long,
        progressStatus: ProgressStatus
    ): List<GroupPurchaseParticipant>

    @Query(
        """
        SELECT g
        FROM GroupPurchaseParticipant g
        JOIN FETCH g.member
        WHERE g.groupPurchase.groupId = :groupPurchaseId
          AND (:cursorId IS NULL OR g.groupParticipantId < :cursorId)
          AND (:deposit IS NULL OR g.deposit = :deposit)
          AND (g.participationStatus = 'JOINED')
          AND (g.member.memberId != :memberId)
        ORDER BY g.groupParticipantId DESC
        """
    )
    fun findParticipantsByCursor(
        @Param("groupPurchaseId") groupPurchaseId: Long,
        @Param("cursorId") cursorParticipantId: Long?,
        @Param("deposit") deposit: Boolean?,
        @Param("memberId") memberId: Long,
        pageable: Pageable
    ): List<GroupPurchaseParticipant>

    @Query(
        """
    SELECT gpp
    FROM GroupPurchaseParticipant gpp
    JOIN FETCH gpp.groupPurchase gp
    JOIN FETCH gp.chatRoom cr
    WHERE gpp.member.memberId = :memberId
      AND gpp.participationStatus = 'JOINED'
      AND (:cursorId IS NULL OR gpp.groupParticipantId < :cursorId)
      AND gp.progressStatus IN :statuses
      AND gp.deleted = false
    ORDER BY gpp.groupParticipantId DESC
    """
    )
    fun findByMemberWithCursor(
        cursorId: Long?,
        memberId: Long,
        statuses: List<ProgressStatus>,
        pageable: Pageable
    ): List<GroupPurchaseParticipant>

    fun countByGroupPurchaseAndParticipationStatus(
        groupPurchase: GroupPurchase,
        participationStatus: ParticipationStatus
    ): Long
}
