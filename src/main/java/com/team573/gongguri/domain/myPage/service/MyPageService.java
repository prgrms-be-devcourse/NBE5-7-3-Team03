package com.team573.gongguri.domain.myPage.service;

import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseResponseDto;
import com.team573.gongguri.domain.groupPurchase.entity.GroupPurchase;
import com.team573.gongguri.domain.groupPurchase.entity.GroupPurchaseParticipant;
import com.team573.gongguri.domain.groupPurchase.entity.ProgressStatus;
import com.team573.gongguri.domain.groupPurchase.mapper.GroupPurchaseMapper;
import com.team573.gongguri.domain.groupPurchase.repository.GroupPurchaseParticipantRepository;
import com.team573.gongguri.domain.groupPurchase.repository.GroupPurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final GroupPurchaseRepository groupPurchaseRepository;
    private final GroupPurchaseParticipantRepository groupPurchaseParticipantRepository;

    // 내가 작성한 공구글
    public List<GroupPurchaseResponseDto> findMyCreatedPurchases(Long memberId, String statusFilter){

        ProgressStatus status = null;

        if( !statusFilter.equals("ALL") ){
            status = ProgressStatus.valueOf(statusFilter);
        }

        List<GroupPurchase> purchases;
        if(status != null) { // 'ALL'
            purchases = groupPurchaseRepository.findByMember_MemberIdAndProgressStatus(memberId, status);
        }else{
            purchases = groupPurchaseRepository.findByMember_MemberId(memberId);
        }

        return purchases.stream()
                .map(GroupPurchaseMapper::toDto)
                .toList();
    }

    // 내가 참여한 공구글
    public List<GroupPurchaseResponseDto> findMyParticipatedPurchases(Long memberId){

        // '본인이 참여했으며, 연관된 공구가 완료된' 공구 참여자 entity 조회
        List<GroupPurchaseParticipant> participants =
                groupPurchaseParticipantRepository.findByMember_MemberIdAndGroupPurchase_ProgressStatus(
                        memberId, ProgressStatus.COMPLETED);

        // 필터링 entity 로부터 공구글 추출, DTO 변환
        return participants.stream()
                .map(GroupPurchaseParticipant::getGroupPurchase)
                .map(GroupPurchaseMapper::toDto)
                .toList();
    }

}
