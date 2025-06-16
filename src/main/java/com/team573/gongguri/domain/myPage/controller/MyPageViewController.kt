package com.team573.gongguri.domain.myPage.controller

import com.team573.gongguri.domain.grouppurchase.dto.GroupPurchaseListResponseDto
import com.team573.gongguri.domain.grouppurchase.dto.GroupPurchaseWithReviewedResponseDto
import com.team573.gongguri.domain.grouppurchase.entity.PurchaseFilter
import com.team573.gongguri.domain.grouppurchase.service.GroupPurchaseService
import com.team573.gongguri.domain.member.dto.LikeInfoDto
import com.team573.gongguri.domain.member.service.MemberService
import com.team573.gongguri.domain.myPage.service.MyPageService
import com.team573.gongguri.global.security.CustomUserDetails
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/my-page")
class MyPageViewController(
    private val myPageService: MyPageService,
    private val memberService: MemberService,
    private val groupPurchaseService: GroupPurchaseService
) {
    @GetMapping("")
    fun showMyPageForm(model: Model, @AuthenticationPrincipal userDetails: CustomUserDetails): String {
        val memberId = userDetails.memberId
        val likeInfo: LikeInfoDto = memberService.getLikeInfo(memberId)
        model.addAttribute("likeCount", likeInfo.likeCount)
        model.addAttribute("dislikeCount", likeInfo.dislikeCount)
        model.addAttribute("nickname", userDetails.nickname)
        return "/myPage/main"
    }

    @GetMapping("/profile")
    fun showMyProfile(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @RequestParam(defaultValue = "ALL") status: PurchaseFilter,
        model: Model
    ): String {
        val memberId = userDetails.memberId
        val createdList: List<GroupPurchaseListResponseDto> = groupPurchaseService.findCreatedPurchases(memberId, status)
        val likeInfo: LikeInfoDto = memberService.getLikeInfo(memberId)

        model.addAttribute("nickname", userDetails.nickname)
        model.addAttribute("likeCount", likeInfo.likeCount)
        model.addAttribute("dislikeCount", likeInfo.dislikeCount)
        model.addAttribute("status", status.name)
        model.addAttribute("createdList", createdList)

        return "myPage/profile"
    }

    @GetMapping("/purchase")
    fun showMyPurchase(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        model: Model
    ): String {
        val memberId = userDetails.memberId
        val participatedList: List<GroupPurchaseWithReviewedResponseDto> = myPageService.findMyParticipatedPurchases(memberId)

        model.addAttribute("participatedList", participatedList)
        return "myPage/purchase"
    }
}
