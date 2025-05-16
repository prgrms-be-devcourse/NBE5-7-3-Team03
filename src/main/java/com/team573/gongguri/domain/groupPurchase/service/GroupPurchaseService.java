package com.team573.gongguri.domain.groupPurchase.service;

import com.team573.gongguri.domain.chat.entity.ChatRoom;
import com.team573.gongguri.domain.chat.repository.ChatRoomRepository;
import com.team573.gongguri.domain.chat.service.ChatService;
import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseRequestDto;
import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseResponseDto;
import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseWithChatResponseDto;
import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseWithParticipantCountDto;
import com.team573.gongguri.domain.groupPurchase.entity.GroupPurchase;
import com.team573.gongguri.domain.groupPurchase.entity.ProgressStatus;
import com.team573.gongguri.domain.groupPurchase.mapper.GroupPurchaseMapper;
import com.team573.gongguri.domain.groupPurchase.repository.GroupPurchaseJpqlRepository;
import com.team573.gongguri.domain.groupPurchase.repository.GroupPurchaseRepository;
import com.team573.gongguri.domain.member.entity.Member;
import com.team573.gongguri.domain.member.entity.Univ;
import com.team573.gongguri.domain.member.repository.MemberRepository;
import com.team573.gongguri.global.exception.ErrorCode;
import com.team573.gongguri.global.exception.ErrorException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupPurchaseService {
    private final GroupPurchaseRepository repository;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatService chatService;
    private final GroupPurchaseJpqlRepository groupPurchaseJpqlRepository;

    @Transactional
    public GroupPurchaseResponseDto add(GroupPurchaseRequestDto dto) {
        Member writer = memberRepository.findById(1L)  // 로그인 연동 전 테스트용 고정 ID
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_MEMBER));

        Univ univ = writer.getUniv();

        ChatRoom chatRoom = chatRoomRepository.findById(1L)
            .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_CHATROOM));

        try {

            GroupPurchase entity = GroupPurchaseMapper.toEntity(dto, writer, chatRoom, univ);
            repository.save(entity);
            return GroupPurchaseMapper.toDto(entity);
        } catch (Exception e) {
            throw new ErrorException(ErrorCode.CREATE_FAILED_GROUP_PURCHASE);
        }
    }

    @Transactional(readOnly = true)
    public GroupPurchaseResponseDto get(Long id) {
        GroupPurchase entity = repository.findById(id)
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_GROUP_PURCHASE));
        return GroupPurchaseMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<GroupPurchaseResponseDto> getAll() {
        return repository.findAll().stream()
                .map(GroupPurchaseMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public GroupPurchaseResponseDto update(Long id, GroupPurchaseRequestDto dto) {
        try {
            GroupPurchase entity = repository.findById(id)
                    .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_GROUP_PURCHASE));

            entity.update(
                    dto.title(),
                    dto.content(),
                    dto.price(),
                    dto.maxParticipants(),
                    dto.bank(),
                    dto.account(),
                    ProgressStatus.valueOf(dto.progressStatus().toUpperCase())
            );
            entity.setImageUrl(dto.imageUrl()); // 업데이트 시에도 imageUrl 반영

            return GroupPurchaseMapper.toDto(entity);
        } catch (Exception e) {
            throw new ErrorException(ErrorCode.UPDATE_FAILED_GROUP_PURCHASE);
        }
    }

    @Transactional
    public void delete(Long id) {
        try {
            GroupPurchase entity = repository.findById(id)
                    .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_GROUP_PURCHASE));
            repository.delete(entity);
        } catch (Exception e) {
            throw new ErrorException(ErrorCode.DELETE_FAILED_GROUP_PURCHASE);
        }
    }

    public List<GroupPurchaseWithChatResponseDto> getWithMessage(
        Integer size,
        Long cursorId,
        List<ProgressStatus> statuses,
        Long memberId
    ) {
        // 공동 구매 조회
        List<GroupPurchaseWithParticipantCountDto> groupPurchases
            = groupPurchaseJpqlRepository.findWithCursorAndParticipantCount(cursorId, memberId, statuses, size);

        // 조회한 공동 구매 채팅 메시지 조회
        List<Long> chatRoomIds = groupPurchases.stream()
            .map(GroupPurchaseWithParticipantCountDto::chatRoomId)
            .toList();

        // 맵으로 채팅 메시지 가져오기
        Map<Long, String> firstMessages = chatService.getFirstMessageMap(chatRoomIds);

        return groupPurchases.stream()
            .map(groupPurchase -> GroupPurchaseMapper.toWithMessageResponseDto(groupPurchase,
                firstMessages))
            .toList();
    }
}
