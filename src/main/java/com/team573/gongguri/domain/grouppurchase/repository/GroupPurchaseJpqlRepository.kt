package com.team573.gongguri.domain.grouppurchase.repository

import com.team573.gongguri.domain.grouppurchase.dto.GroupPurchaseWithParticipantCountDto
import com.team573.gongguri.domain.grouppurchase.entity.ProgressStatus
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository

@Repository
class GroupPurchaseJpqlRepository(
    @PersistenceContext private val em: EntityManager
) {

    fun findAllWithCursorAndParticipantCount(
        cursorId: Long?,
        statuses: List<ProgressStatus>,
        size: Int
    ): List<GroupPurchaseWithParticipantCountDto> {
        val jpql = """
            SELECT new com.team573.gongguri.domain.grouppurchase.dto.GroupPurchaseWithParticipantCountDto(
                gp.groupId,
                gp.title,
                gp.content,
                gp.price,
                gp.maxParticipants,
                gp.progressStatus,
                gp.createdAt,
                null,
                COUNT(p),
                gp.imageUrl
            )
            FROM GroupPurchase gp
            LEFT JOIN GroupPurchaseParticipant p ON p.groupPurchase.id = gp.id AND p.participationStatus = 'JOINED'
            WHERE (:cursorId IS NULL OR gp.groupId < :cursorId)
            AND (:statusesIsEmpty = true OR gp.progressStatus IN :statuses)
            AND gp.deleted = false
            GROUP BY gp.groupId
            ORDER BY gp.groupId DESC
        """

        return em.createQuery(jpql, GroupPurchaseWithParticipantCountDto::class.java)
            .setParameter("cursorId", cursorId)
            .setParameter("statusesIsEmpty", statuses.isEmpty())
            .setParameter("statuses", statuses)
            .setMaxResults(size)
            .resultList
    }
}
