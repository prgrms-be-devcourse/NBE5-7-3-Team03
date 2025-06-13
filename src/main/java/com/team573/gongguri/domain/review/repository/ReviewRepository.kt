package com.team573.gongguri.domain.review.repository;

import com.team573.gongguri.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Boolean existsByGroupPurchase_groupIdAndMember_memberId(Long groupId, Long memberId);
}
