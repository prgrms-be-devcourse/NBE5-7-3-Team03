package com.team573.gongguri.domain.review.service;

import com.team573.gongguri.domain.grouppurchase.entity.GroupPurchase;
import com.team573.gongguri.domain.grouppurchase.entity.ProgressStatus;
import com.team573.gongguri.domain.grouppurchase.repository.GroupPurchaseRepository;
import com.team573.gongguri.domain.member.entity.Member;
import com.team573.gongguri.domain.member.repository.MemberRepository;
import com.team573.gongguri.domain.review.entity.Review;
import com.team573.gongguri.domain.review.mapper.ReviewMapper;
import com.team573.gongguri.domain.review.repository.ReviewRepository;
import com.team573.gongguri.global.exception.CustomErrorCode;
import com.team573.gongguri.global.exception.CustomException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final GroupPurchaseRepository groupPurchaseRepository;

    @Transactional
    public Long addReview(Long groupPurchaseId, Long memberId, Boolean like) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_MEMBER));
        GroupPurchase groupPurchase = groupPurchaseRepository.findById(groupPurchaseId)
            .orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_GROUP_PURCHASE));

        if (!groupPurchase.getProgressStatus().equals(ProgressStatus.COMPLETED)) {
            throw new CustomException(CustomErrorCode.IS_NOT_COMPLETED);
        }

        Review createdReview = reviewRepository.save(ReviewMapper.toEntity(groupPurchase, member, like));

        groupPurchase.getMember().updateLikeCount(like);

        return createdReview.getReviewId();
    }
}
