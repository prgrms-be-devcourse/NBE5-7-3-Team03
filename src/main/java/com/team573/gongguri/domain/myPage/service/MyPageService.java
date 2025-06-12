package com.team573.gongguri.domain.myPage.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.team573.gongguri.domain.grouppurchase.dto.GroupPurchaseWithReviewedResponseDto;
import com.team573.gongguri.domain.grouppurchase.entity.GroupPurchase;
import com.team573.gongguri.domain.grouppurchase.entity.GroupPurchaseParticipant;
import com.team573.gongguri.domain.grouppurchase.entity.ParticipationStatus;
import com.team573.gongguri.domain.grouppurchase.entity.ProgressStatus;
import com.team573.gongguri.domain.grouppurchase.mapper.GroupPurchaseMapperKt;
import com.team573.gongguri.domain.grouppurchase.repository.GroupPurchaseParticipantRepository;
import com.team573.gongguri.domain.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final GroupPurchaseParticipantRepository groupPurchaseParticipantRepository;
    private final ReviewRepository reviewRepository;

    // 내가 참여한 공구글
    public List<GroupPurchaseWithReviewedResponseDto> findMyParticipatedPurchases(Long memberId){

        // '본인이 참여했으며, 연관된 공구가 완료된' 공구 참여자 entity 조회
        List<GroupPurchaseParticipant> participants =
                groupPurchaseParticipantRepository.findByMember_MemberIdAndGroupPurchase_ProgressStatus(
                        memberId, ProgressStatus.COMPLETED);

        // 필터링 entity 로부터 공구글 추출, DTO 변환
        return participants.stream()
                .map(GroupPurchaseParticipant::getGroupPurchase)
                .map(purchase -> toWithReviewedDto(purchase, memberId))
                .toList();
    }

    private GroupPurchaseWithReviewedResponseDto toWithReviewedDto(GroupPurchase groupPurchase, Long memberId) {
        Long participantCount = groupPurchaseParticipantRepository.countByGroupPurchaseAndParticipationStatus(
            groupPurchase, ParticipationStatus.JOINED);

        boolean isReviewed = reviewRepository.existsByGroupPurchase_groupIdAndMember_memberId(
            groupPurchase.getGroupId(), memberId);

        return GroupPurchaseMapperKt.toDtoWithReviewed(groupPurchase, participantCount, isReviewed);
    }

}
