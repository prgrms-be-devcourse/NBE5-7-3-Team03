package com.team573.gongguri.domain.groupPurchase.repository;

import com.team573.gongguri.domain.groupPurchase.entity.GroupPurchase;
import com.team573.gongguri.domain.groupPurchase.entity.ProgressStatus;
import java.util.List;
import org.springframework.data.domain.Pageable;
import com.team573.gongguri.domain.groupPurchase.entity.ProgressStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupPurchaseRepository extends JpaRepository<GroupPurchase, Long> {
    List<GroupPurchase> findByMember_MemberIdAndProgressStatus(Long memberId, ProgressStatus status);

    List<GroupPurchase> findByMember_MemberId(Long memberId);

    @Query("""
        SELECT gp FROM GroupPurchase gp
        JOIN FETCH gp.chatRoom cr
        WHERE (:cursorId IS NULL OR gp.groupId < :cursorId)
        AND (:status IS NULL OR gp.progressStatus = :status)
        ORDER BY gp.groupId DESC
    """)
    List<GroupPurchase> findByCursorWithChatRoom(
        @Param("cursorId") Long cursorId,
        @Param("status") ProgressStatus status,
        Pageable pageable
    );

}
