package com.team573.gongguri.domain.grouppurchase.controller

import com.team573.gongguri.domain.member.repository.MemberRepository
import com.team573.gongguri.global.exception.CustomErrorCode
import com.team573.gongguri.global.exception.CustomException
import com.team573.gongguri.global.security.CustomUserDetails
import lombok.RequiredArgsConstructor
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping


@Controller
@RequestMapping("/group-purchase")
class GroupPurchaseViewController (
    private val memberRepository: MemberRepository
) {


    // 공동구매 목록 페이지
    @GetMapping("")
    fun showHomePage(@AuthenticationPrincipal userDetails: CustomUserDetails, model: Model): String {
        val member = memberRepository.findById(userDetails.memberId)
            .orElseThrow { CustomException(CustomErrorCode.NOT_FOUND_MEMBER) }

        model.addAttribute("univName", member.univ.univName)
        return "groupPurchase/group-purchase"
    }

    // 공동구매 게시글 작성 페이지
    @GetMapping("/post")
    fun showCreatePage(): String {
        return "groupPurchase/group-purchase-post"
    }

    // 공동구매 게시글 상세 페이지
    @GetMapping("/{id}")
    fun showDetailPage(@PathVariable id: Long, model: Model): String {
        model.addAttribute("groupId", id)
        return "groupPurchase/group-purchase-read"
    }

    // 공동구매 게시글 수정 페이지
    @GetMapping("/update")
    fun showUpdatePage(): String {
        return "groupPurchase/group-purchase-update"
    }

    @GetMapping("/chats")
    fun showChats(): String {
        return "groupPurchase/group-purchase-chats"
    }

    @GetMapping("/{groupPurchaseId}/participants")
    fun manageParticipants(@PathVariable groupPurchaseId: Long?, model: Model): String {
        model.addAttribute("groupPurchaseId", groupPurchaseId)
        return "groupPurchase/participants-manage"
    }
}
