package com.team573.gongguri.domain.groupPurchase.controller;

import com.team573.gongguri.domain.chat.repository.ChatRoomRepository;
import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseRequestDto;
import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseResponseDto;
import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseWithChatResponseDto;
import com.team573.gongguri.domain.groupPurchase.entity.ProgressStatus;
import com.team573.gongguri.domain.groupPurchase.service.GroupPurchaseService;
import com.team573.gongguri.domain.member.repository.MemberRepository;
import com.team573.gongguri.domain.member.repository.UnivRepository;
import com.team573.gongguri.global.security.CustomUserDetails;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/group-purchases")
@RequiredArgsConstructor
@Slf4j
public class GroupPurchaseController {
    private final GroupPurchaseService service;
    private final MemberRepository memberRepository;
    private final UnivRepository univRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final GroupPurchaseService groupPurchaseService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupPurchaseResponseDto> add(@RequestBody GroupPurchaseRequestDto dto) {
        log.info("[GroupPurchaseController] JSON 방식 게시글 작성 요청 수신");
        GroupPurchaseResponseDto createdDto = service.add(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupPurchaseResponseDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @GetMapping
    public ResponseEntity<List<GroupPurchaseResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupPurchaseResponseDto> update(@PathVariable Long id,
                                                           @RequestBody GroupPurchaseRequestDto dto) {

        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/chat")
    public ResponseEntity<List<GroupPurchaseWithChatResponseDto>> getWithChat(
        @RequestParam(required = false, name = "cursor") Long cursorGroupPurchaseId,
        @RequestParam(required = false) String progressStatus,
        @RequestParam(defaultValue = "10") int size,
        @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        List<ProgressStatus> progressStatuses = new ArrayList<>();

        if (progressStatus != null) {
            if (progressStatus.equals("RECRUITING")) {
                progressStatuses.add(ProgressStatus.RECRUITING);
                progressStatuses.add(ProgressStatus.CLOSED);
            } else if (progressStatus.equals("COMPLETED")) {
                progressStatuses.add(ProgressStatus.COMPLETED);
            }
        }

        List<GroupPurchaseWithChatResponseDto> withMessages
            = groupPurchaseService.getWithMessage(size, cursorGroupPurchaseId, progressStatuses, customUserDetails.getMemberId());
        return ResponseEntity.ok(withMessages);
    }
}
