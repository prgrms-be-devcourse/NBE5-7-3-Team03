package com.team573.gongguri.domain.groupPurchase.repository;

import com.team573.gongguri.domain.groupPurchase.entity.GroupPurchase;
import com.team573.gongguri.domain.groupPurchase.entity.ProgressStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupPurchaseRepository extends JpaRepository<GroupPurchase, Long> {
    List<GroupPurchase> findByMember_MemberIdAndProgressStatus(Long memberId, ProgressStatus status);

    List<GroupPurchase> findByMember_MemberId(Long memberId);
}
