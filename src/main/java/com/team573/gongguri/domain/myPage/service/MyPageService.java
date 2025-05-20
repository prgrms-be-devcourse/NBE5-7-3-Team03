package com.team573.gongguri.domain.myPage.service;


import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseListResponseDto;
import com.team573.gongguri.domain.groupPurchase.entity.GroupPurchaseParticipant;
import com.team573.gongguri.domain.groupPurchase.entity.ProgressStatus;
import com.team573.gongguri.domain.groupPurchase.mapper.GroupPurchaseMapper;
import com.team573.gongguri.domain.groupPurchase.repository.GroupPurchaseParticipantRepository;
import com.team573.gongguri.domain.member.repository.MemberRepository;
import com.team573.gongguri.global.exception.CustomErrorCode;
import com.team573.gongguri.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final GroupPurchaseParticipantRepository groupPurchaseParticipantRepository;
    private final MemberRepository memberRepository;

    // 내가 참여한 공구글
    public List<GroupPurchaseListResponseDto> findMyParticipatedPurchases(Long memberId){

        memberRepository.findById(memberId).orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_MEMBER));

        // '본인이 참여했으며, 연관된 공구가 완료된' 공구 참여자 entity 조회
        List<GroupPurchaseParticipant> participants =
                groupPurchaseParticipantRepository.findByMember_MemberIdAndGroupPurchase_ProgressStatus(
                        memberId, ProgressStatus.COMPLETED);

        // 필터링 entity 로부터 공구글 추출, DTO 변환
        return participants.stream()
                .map(GroupPurchaseParticipant::getGroupPurchase)
                .map(purchase -> {
                    int currentParticipants = groupPurchaseParticipantRepository.countByGroupPurchase_GroupId(purchase.getGroupId());
                    return GroupPurchaseMapper.toListDto(purchase, currentParticipants);
                })
                .toList();
    }

}
