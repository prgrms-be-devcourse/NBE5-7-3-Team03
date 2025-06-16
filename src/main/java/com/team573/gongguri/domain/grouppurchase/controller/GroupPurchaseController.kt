package com.team573.gongguri.domain.grouppurchase.controller

import com.team573.gongguri.domain.grouppurchase.dto.*
import com.team573.gongguri.domain.grouppurchase.entity.PurchaseFilter
import com.team573.gongguri.domain.grouppurchase.service.GroupPurchaseParticipantService
import com.team573.gongguri.domain.grouppurchase.service.GroupPurchaseService
import com.team573.gongguri.global.security.CustomUserDetails
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/group-purchases")
class GroupPurchaseController(
    private val groupPurchaseService: GroupPurchaseService,
    private val groupPurchaseParticipantService: GroupPurchaseParticipantService
) {

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun add(
        @RequestBody dto: GroupPurchaseRequestDto,
        @AuthenticationPrincipal customUserDetails: CustomUserDetails
    ): ResponseEntity<GroupPurchaseCreateResponseDto> {
        log.info("[GroupPurchaseController] JSON 방식 게시글 작성 요청 수신")
        val memberId = customUserDetails.memberId
        val createdDto = groupPurchaseService.add(dto, memberId)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDto)
    }

    @GetMapping("/{id}")
    operator fun get(
        @PathVariable id: Long,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<GroupPurchaseDetailResponseDto> {
        val memberId = userDetails.memberId
        return ResponseEntity.ok(groupPurchaseService[id, memberId])
    }


    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody dto: GroupPurchaseRequestDto
    ): ResponseEntity<GroupPurchaseUpdateResponseDto> {
        return ResponseEntity.ok(groupPurchaseService.update(id, dto))
    }


    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: Long,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<Void> {
        val memberId = userDetails.memberId
        groupPurchaseService.delete(id, memberId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{id}/join")
    fun join(
        @PathVariable id: Long,
        @AuthenticationPrincipal customUserDetails: CustomUserDetails
    ): ResponseEntity<Void> {
        val memberId = customUserDetails.memberId
        groupPurchaseService.join(id, memberId)
        log.info("member joined: {}", memberId)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/chat")
    fun getWithChat(
        @RequestParam(required = false, name = "cursor") cursorGroupPurchaseId: Long?,
        @RequestParam(required = false, defaultValue = "ALL", name = "progressStatus") purchaseFilter: PurchaseFilter,
        @RequestParam(defaultValue = "10") size: Int,
        @AuthenticationPrincipal customUserDetails: CustomUserDetails
    ): ResponseEntity<List<GroupPurchaseWithChatResponseDto>> {
        val withMessages = groupPurchaseService.getWithMessage(
            size,
            cursorGroupPurchaseId,
            purchaseFilter,
            customUserDetails.memberId
        )
        return ResponseEntity.ok(withMessages)
    }

    @PatchMapping("/{groupPurchaseId}/participants/{participantsId}/confirm")
    fun confirmDeposit(
        @PathVariable groupPurchaseId: Long,
        @PathVariable participantsId: Long,
        @AuthenticationPrincipal customUserDetails: CustomUserDetails
    ): ResponseEntity<Void> {
        groupPurchaseParticipantService.confirmDeposit(groupPurchaseId, participantsId, customUserDetails.memberId)
        return ResponseEntity.noContent().build()
    }

    @PatchMapping("/{groupPurchaseId}/participants/{participantsId}/cancel")
    fun cancelParticipantStatus(
        @PathVariable groupPurchaseId: Long,
        @PathVariable participantsId: Long,
        @AuthenticationPrincipal customUserDetails: CustomUserDetails
    ): ResponseEntity<Void> {
        groupPurchaseParticipantService.cancelParticipation(
            groupPurchaseId,
            participantsId,
            customUserDetails.memberId
        )
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{groupPurchaseId}/participants")
    fun getParticipants(
        @PathVariable groupPurchaseId: Long,
        @RequestParam(required = false, name = "cursor") cursorParticipantId: Long?,
        @RequestParam(required = false) deposit: Boolean?,
        @RequestParam(defaultValue = "10") size: Int,
        @AuthenticationPrincipal customUserDetails: CustomUserDetails
    ): ResponseEntity<List<GroupPurchaseParticipantResponseDto>> {
        val participants = groupPurchaseParticipantService.getParticipants(
            groupPurchaseId, cursorParticipantId, deposit, customUserDetails.memberId, size
        )

        return ResponseEntity.ok(participants)
    }

    @GetMapping
    fun getAllByCursor(
        @RequestParam(required = false, name = "cursor") cursorGroupPurchaseId: Long?,
        @RequestParam(required = false, defaultValue = "ALL", name = "purchaseFilter") purchaseFilter: PurchaseFilter,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<List<GroupPurchaseListResponseDto>> {
        val progressStatuses = purchaseFilter.toStatuses()

        val groupPurchases = groupPurchaseService.getAllByCursor(
            cursorGroupPurchaseId,
            progressStatuses,
            size
        )

        return ResponseEntity.ok(groupPurchases)
    }
    companion object {
        private val log: Logger = LoggerFactory.getLogger(GroupPurchaseController::class.java)
    }
}

