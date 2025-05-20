package com.team573.gongguri.domain.groupPurchase.service;

import com.team573.gongguri.domain.chat.entity.ChatRoom;
import com.team573.gongguri.domain.chat.service.ChatService;
import com.team573.gongguri.domain.groupPurchase.dto.*;
import com.team573.gongguri.domain.groupPurchase.entity.GroupPurchase;
import com.team573.gongguri.domain.groupPurchase.entity.GroupPurchaseParticipant;
import com.team573.gongguri.domain.groupPurchase.entity.ParticipationStatus;
import com.team573.gongguri.domain.groupPurchase.entity.ProgressStatus;
import com.team573.gongguri.domain.groupPurchase.entity.PurchaseFilter;
import com.team573.gongguri.domain.groupPurchase.mapper.GroupPurchaseMapper;
import com.team573.gongguri.domain.groupPurchase.mapper.GroupPurchaseParticipantMapper;
import com.team573.gongguri.domain.groupPurchase.repository.GroupPurchaseJpqlRepository;
import com.team573.gongguri.domain.groupPurchase.repository.GroupPurchaseParticipantRepository;
import com.team573.gongguri.domain.groupPurchase.repository.GroupPurchaseRepository;
import com.team573.gongguri.domain.member.entity.Member;
import com.team573.gongguri.domain.member.entity.Univ;
import com.team573.gongguri.domain.member.repository.MemberRepository;
import com.team573.gongguri.domain.member.service.MemberService;
import com.team573.gongguri.global.exception.CustomErrorCode;
import com.team573.gongguri.global.exception.CustomException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupPurchaseService {
    private final GroupPurchaseRepository groupPurchaseRepository;
    private final GroupPurchaseParticipantRepository participantRepository;
    private final MemberRepository memberRepository;
    private final ChatService chatService;
    private final GroupPurchaseJpqlRepository groupPurchaseJpqlRepository;
    private final GroupPurchaseParticipantRepository groupPurchaseParticipantRepository;
    private final MemberService memberService;


    private GroupPurchase getActiveGroupPurchase(Long id) {
        GroupPurchase groupPurchase = groupPurchaseRepository.findByGroupIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_GROUP_PURCHASE));
        return groupPurchase;
    }

    private int getCurrentParticipantsCount(Long id) {
        int currentParticipants = participantRepository.countByGroupPurchase_GroupId(id);
        return currentParticipants;
    }

    private void registerParticipant(GroupPurchase groupPurchase, Member member) {
        try {
            GroupPurchaseParticipant participant = GroupPurchaseParticipantMapper.toEntity(groupPurchase, member);
            participantRepository.save(participant);
        } catch (Exception e) {
            log.error("참여자 등록 실패", e);
            throw new CustomException(CustomErrorCode.JOIN_FAILED);
        }
    }

    @Transactional
    public GroupPurchaseCreateResponseDto add(GroupPurchaseRequestDto dto, Long memberId) {
        Member writer = memberService.getMemberById(memberId);

        Univ univ = writer.getUniv();
        ChatRoom chatRoom;
        try {
            chatRoom = chatService.addChatRoom(writer.getEmail());
        } catch (Exception e) {
            log.error("채팅방 생성 실패", e);
            throw new CustomException(CustomErrorCode.CREATE_FAILED_GROUP_PURCHASE);
        }

        GroupPurchase groupPurchase;
        try {
            groupPurchase = GroupPurchaseMapper.toEntity(dto, writer, chatRoom, univ);
            groupPurchase.setImageUrl(dto.imageUrl());
            groupPurchaseRepository.save(groupPurchase);
        } catch (Exception e) {
            log.error("공동구매 게시글 저장 실패", e);
            throw new CustomException(CustomErrorCode.CREATE_FAILED_GROUP_PURCHASE);
        }

        registerParticipant(groupPurchase, writer);
        return GroupPurchaseMapper.toCreateDto(groupPurchase);
    }
    @Transactional(readOnly = true)
    public GroupPurchaseDetailResponseDto get(Long id, Long memberId) {
        GroupPurchase groupPurchase = groupPurchaseRepository.findById(id)
                .orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_GROUP_PURCHASE));
        int currentParticipants = getCurrentParticipantsCount(id);
        boolean isParticipated = participantRepository.existsByGroupPurchase_GroupIdAndMember_MemberId(id, memberId);

        return GroupPurchaseMapper.toDetailDto(groupPurchase, currentParticipants, isParticipated);
    }

    @Transactional(readOnly = true)
    public List<GroupPurchaseListResponseDto> getAllByCursor(
            Long cursorId,
            List<ProgressStatus> statuses,
            int size
    ) {
        List<GroupPurchaseWithParticipantCountDto> groupPurchases =
                groupPurchaseJpqlRepository.findAllWithCursorAndParticipantCount(cursorId, statuses, size);

        return groupPurchases.stream()
                .map(GroupPurchaseMapper::toListDto)
                .toList();
    }

    @Transactional
    public GroupPurchaseUpdateResponseDto update(Long id, GroupPurchaseRequestDto dto) {

        GroupPurchase groupPurchase = getActiveGroupPurchase(id);
        try {
            groupPurchase.update(
                    dto.title(),
                    dto.content(),
                    dto.price(),
                    dto.maxParticipants(),
                    dto.bank(),
                    dto.account(),
                    ProgressStatus.valueOf(dto.progressStatus().toUpperCase())
            );
            groupPurchase.setImageUrl(dto.imageUrl());
        } catch (Exception e) {
            log.error("공동구매 수정 실패", e);
            throw new CustomException(CustomErrorCode.UPDATE_FAILED_GROUP_PURCHASE);
        }
        return GroupPurchaseMapper.toUpdateDto(groupPurchase);
    }

    @Transactional
    public void delete(Long id) {
        GroupPurchase groupPurchase = getActiveGroupPurchase(id);
        groupPurchase.markAsDeleted();
    }

    @Transactional
    public void join(Long groupId, Long memberId) {
        Member member = memberService.getMemberById(memberId);

        GroupPurchase groupPurchase = getActiveGroupPurchase(groupId);

        int currentCount = countParticipantsByStatus(groupPurchase, ParticipationStatus.JOINED).intValue();
        if (currentCount >= groupPurchase.getMaxParticipants()) {
            throw new CustomException(CustomErrorCode.PARTICIPANT_LIMIT_REACHED);
        }

        boolean alreadyJoined = participantRepository.existsByGroupPurchase_GroupIdAndMember_MemberId(groupId, memberId);
        if (alreadyJoined) {
            throw new CustomException(CustomErrorCode.ALREADY_JOINED);
        }

        registerParticipant(groupPurchase, member);
        chatService.addChatParticipation(groupPurchase.getChatRoom().getChatRoomId(), member.getEmail());
        int afterJoinCount = currentCount + 1;
        if (afterJoinCount >= groupPurchase.getMaxParticipants()) {
            groupPurchase.setProgressStatus(ProgressStatus.CLOSED);
        }
    }

    @Transactional(readOnly = true)
    public List<GroupPurchaseWithChatResponseDto> getWithMessage(
        Integer size,
        Long cursorId,
        PurchaseFilter purchaseFilter,
        Long memberId
    ) {
        // 필터로 공동 구매 상태 구하기
        List<ProgressStatus> statuses = purchaseFilter.toStatuses();

        PageRequest pageable = PageRequest.of(0, size);

        // 공동 구매 조회
        List<GroupPurchase> groupPurchases
            = groupPurchaseRepository.findWithCursorAndParticipantCount(cursorId, memberId, statuses, pageable);

        // 맵으로 채팅 메시지 가져오기
        Map<Long, String> firstMessages = getFirstMessages(groupPurchases);

        return groupPurchases.stream()
            .map(groupPurchase -> GroupPurchaseMapper.toDtoWithMessage(
                groupPurchase,
                countParticipantsByStatus(groupPurchase, ParticipationStatus.JOINED),
                firstMessages
                )
            )
            .toList();
    }

    // 조회한 공동 구매 채팅 메시지 조회
    private Map<Long, String> getFirstMessages(List<GroupPurchase> groupPurchases) {
        List<Long> chatRoomIds = groupPurchases.stream()
            .map(groupPurchase -> groupPurchase.getChatRoom().getChatRoomId())
            .toList();
        return chatService.getFirstMessageMap(chatRoomIds);
    }

    // ParticipationStatus 로 해당 공동 구매 참여자 수 조회
    private Long countParticipantsByStatus(GroupPurchase groupPurchase, ParticipationStatus status) {
        return groupPurchaseParticipantRepository.countByGroupPurchaseAndParticipationStatus(
            groupPurchase,
            status
        );
    }

    @Transactional(readOnly = true)
    public GroupPurchaseSimpleResponseDto getSimpleInfo(Long groupPurchaseId) {
        GroupPurchase groupPurchase = groupPurchaseRepository.findById(groupPurchaseId)
            .orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_GROUP_PURCHASE));

        Long participantCount
            = groupPurchaseParticipantRepository.countByGroupPurchaseAndParticipationStatus(groupPurchase, ParticipationStatus.JOINED);

        return GroupPurchaseMapper.toDtoWithCount(groupPurchase, participantCount);
    }

    //특정 멤버가 작성한 공동구매글 조회
    public List<GroupPurchaseListResponseDto> findCreatedPurchases(Long memberId, PurchaseFilter purchaseFilter){

        memberRepository.findById(memberId).orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_MEMBER));


        List<GroupPurchase> purchases;
        List<ProgressStatus> statuses = purchaseFilter.toStatuses();

        if (statuses.size() == ProgressStatus.values().length) {
            purchases = groupPurchaseRepository.findByMember_MemberId(memberId);
        } else {
            purchases = groupPurchaseRepository.findByMember_MemberIdAndProgressStatusIn(memberId, statuses);
        }

        return purchases.stream()
                .map(purchase -> {
                    int currentParticipants = participantRepository.countByGroupPurchase_GroupId(purchase.getGroupId());
                    return GroupPurchaseMapper.toListDto(purchase, currentParticipants);

                })
                .toList();
    }
}
