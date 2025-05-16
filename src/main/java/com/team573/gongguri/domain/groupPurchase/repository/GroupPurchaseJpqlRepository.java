package com.team573.gongguri.domain.groupPurchase.repository;

import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseWithParticipantCountDto;
import com.team573.gongguri.domain.groupPurchase.entity.ProgressStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class GroupPurchaseJpqlRepository {

    @PersistenceContext
    private EntityManager em;

    public List<GroupPurchaseWithParticipantCountDto> findWithCursorAndParticipantCount(
        Long cursorId,
        Long memberId,
        List<ProgressStatus> statuses,
        int size
    ) {

        String jpql = """
            SELECT new com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseWithParticipantCountDto(
                gp.groupId,
                gp.title,
                gp.content,
                gp.price,
                gp.maxParticipants,
                gp.progressStatus,
                gp.createdAt,
                cr.id,
                COUNT(p),
                gp.imageUrl
            )
            FROM GroupPurchase gp
            JOIN gp.chatRoom cr
            LEFT JOIN GroupPurchaseParticipant p on p.groupPurchase.id = gp.id AND p.participationStatus = 'JOINED'
            WHERE (:cursorId IS NULL OR gp.groupId < :cursorId)
            AND (:statusesIsEmpty = true OR gp.progressStatus IN :statuses)
            AND gp.groupId IN (
                    SELECT gpp.groupPurchase.groupId
                    FROM GroupPurchaseParticipant gpp
                    WHERE gpp.member.id = :memberId
                    AND gpp.participationStatus = 'JOINED'
                )
            GROUP BY gp.groupId
            ORDER BY gp.groupId DESC
            """;

        return em.createQuery(jpql, GroupPurchaseWithParticipantCountDto.class)
            .setParameter("cursorId", cursorId)
            .setParameter("statusesIsEmpty", statuses == null || statuses.isEmpty())
            .setParameter("statuses", statuses == null ? Collections.emptyList() : statuses)
            .setParameter("memberId", memberId)
            .setMaxResults(size)
            .getResultList();
    }
}
