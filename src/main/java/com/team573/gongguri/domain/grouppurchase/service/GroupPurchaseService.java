package com.team573.gongguri.domain.grouppurchase.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team573.gongguri.domain.chat.entity.ChatRoom;
import com.team573.gongguri.domain.chat.service.ChatService;
import com.team573.gongguri.domain.grouppurchase.dto.GroupPurchaseCreateResponseDto;
import com.team573.gongguri.domain.grouppurchase.dto.GroupPurchaseDetailResponseDto;
import com.team573.gongguri.domain.grouppurchase.dto.GroupPurchaseListResponseDto;
import com.team573.gongguri.domain.grouppurchase.dto.GroupPurchaseRequestDto;
import com.team573.gongguri.domain.grouppurchase.dto.GroupPurchaseSimpleResponseDto;
import com.team573.gongguri.domain.grouppurchase.dto.GroupPurchaseUpdateResponseDto;
import com.team573.gongguri.domain.grouppurchase.dto.GroupPurchaseWithChatResponseDto;
import com.team573.gongguri.domain.grouppurchase.dto.GroupPurchaseWithParticipantCountDto;
import com.team573.gongguri.domain.grouppurchase.entity.GroupPurchase;
import com.team573.gongguri.domain.grouppurchase.entity.GroupPurchaseParticipant;
import com.team573.gongguri.domain.grouppurchase.entity.ParticipationStatus;
import com.team573.gongguri.domain.grouppurchase.entity.ProgressStatus;
import com.team573.gongguri.domain.grouppurchase.entity.PurchaseFilter;
import com.team573.gongguri.domain.grouppurchase.mapper.GroupPurchaseMapperKt;
import com.team573.gongguri.domain.grouppurchase.mapper.GroupPurchaseParticipantMapperKt;
import com.team573.gongguri.domain.grouppurchase.repository.GroupPurchaseJpqlRepository;
import com.team573.gongguri.domain.grouppurchase.repository.GroupPurchaseParticipantRepository;
import com.team573.gongguri.domain.grouppurchase.repository.GroupPurchaseRepository;
import com.team573.gongguri.domain.member.entity.Member;
import com.team573.gongguri.domain.member.entity.Univ;
import com.team573.gongguri.domain.member.repository.MemberRepository;
import com.team573.gongguri.domain.member.service.MemberService;
import com.team573.gongguri.global.exception.CustomErrorCode;
import com.team573.gongguri.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
        GroupPurchase groupPurchase = groupPurchaseRepository.findByGroupIdAndDeletedFalse(id)
                .orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_GROUP_PURCHASE));
        return groupPurchase;
    }

    private void registerParticipant(GroupPurchase groupPurchase, Member member) {
        try {
            GroupPurchaseParticipant participant = GroupPurchaseParticipantMapperKt.toEntity(groupPurchase, member);
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
            groupPurchase = GroupPurchaseMapperKt.toEntity(dto, writer, chatRoom, univ);
            groupPurchase.setImageUrl(dto.getImageUrl());
            groupPurchaseRepository.save(groupPurchase);
        } catch (Exception e) {
            log.error("공동구매 게시글 저장 실패", e);
            throw new CustomException(CustomErrorCode.CREATE_FAILED_GROUP_PURCHASE);
        }

        registerParticipant(groupPurchase, writer);
        return GroupPurchaseMapperKt.toCreateDto(groupPurchase);
    }
    @Transactional(readOnly = true)
    public GroupPurchaseDetailResponseDto get(Long id, Long memberId) {
        GroupPurchase groupPurchase = groupPurchaseRepository.findById(id)
                .orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_GROUP_PURCHASE));
        Long currentParticipants = participantRepository.countByGroupPurchaseAndParticipationStatus(groupPurchase, ParticipationStatus.JOINED);
        boolean isParticipated = participantRepository.existsByGroupPurchase_GroupIdAndMember_MemberId(id, memberId);

        return GroupPurchaseMapperKt.toDetailDto(groupPurchase, currentParticipants, isParticipated);
    }

    @Transactional(readOnly = true)
    public List<GroupPurchaseListResponseDto> getAllByCursor(
            Long cursorId,
            List<ProgressStatus> statuses,
            int size
    ) {
        List<GroupPurchaseWithParticipantCountDto> groupPurchases;
        try {
            groupPurchases = groupPurchaseJpqlRepository.findAllWithCursorAndParticipantCount(cursorId, statuses, size);
        } catch (Exception e) {
            log.error("공동구매 목록 조회 실패");
            throw new CustomException(CustomErrorCode.FAILED_GROUP_PURCHASE_LIST);
        }
        return groupPurchases.stream()
                .map(GroupPurchaseMapperKt::toListDto)
                .toList();
    }

    @Transactional
    public GroupPurchaseUpdateResponseDto update(Long id, GroupPurchaseRequestDto dto) {

        GroupPurchase groupPurchase = getActiveGroupPurchase(id);
        try {
            groupPurchase.update(
                    dto.getTitle(),
                    dto.getContent(),
                    dto.getPrice(),
                    dto.getMaxParticipants(),
                    dto.getBank(),
                    dto.getAccount(),
                    ProgressStatus.valueOf(dto.getProgressStatus().toUpperCase())
            );
            groupPurchase.setImageUrl(dto.getImageUrl());
        } catch (Exception e) {
            log.error("공동구매 수정 실패", e);
            throw new CustomException(CustomErrorCode.UPDATE_FAILED_GROUP_PURCHASE);
        }
        return GroupPurchaseMapperKt.toUpdateDto(groupPurchase);
    }

    @Transactional
    public void delete(Long id, Long memberId) {
        GroupPurchase groupPurchase = getActiveGroupPurchase(id);
        if (!groupPurchase.getMember().getMemberId().equals(memberId)) {
            throw new CustomException(CustomErrorCode.UNAUTHORIZED_GROUP_PURCHASE_MANAGE);
        }

        if (!groupPurchase.getProgressStatus().equals(ProgressStatus.COMPLETED)) {
            boolean hasDepositedParticipants = participantRepository
                    .existsByGroupPurchase_GroupIdAndDepositIsTrue(id);
            if (hasDepositedParticipants) {
                throw new CustomException(CustomErrorCode.DELETE_FAILED_WITH_DEPOSITED_PARTICIPANTS);
            }
        }
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

        try {
            chatService.addChatParticipation(groupPurchase.getChatRoom().getChatRoomId(), member.getEmail());
        }catch (Exception e) {
            log.error("채팅방 참여 실패", e);
            throw new CustomException(CustomErrorCode.CHAT_JOIN_FALED);
        }
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
        List<ProgressStatus> statuses = purchaseFilter.toStatuses();

        PageRequest pageable = PageRequest.of(0, size);

        List<GroupPurchaseParticipant> groupPurchaseParticipants
            = groupPurchaseParticipantRepository.findByMemberWithCursor(cursorId, memberId, statuses, pageable);

        List<GroupPurchase> groupPurchases = groupPurchaseParticipants.stream()
            .map(GroupPurchaseParticipant::getGroupPurchase)
            .toList();

        Map<Long, String> firstMessages = getFirstMessages(groupPurchases);

        return groupPurchaseParticipants.stream()
            .map(groupPurchase -> GroupPurchaseMapperKt.toDtoWithMessage(
                groupPurchase,
                countParticipantsByStatus(groupPurchase.getGroupPurchase(), ParticipationStatus.JOINED),
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

        return GroupPurchaseMapperKt.toDtoWithCount(groupPurchase, participantCount);
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
                    Long currentParticipants = participantRepository.countByGroupPurchaseAndParticipationStatus(purchase, ParticipationStatus.JOINED);
                    return GroupPurchaseMapperKt.toListDto(purchase, currentParticipants);

                })
                .toList();
    }
}
