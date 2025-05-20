package com.team573.gongguri.domain.myPage.controller;

import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseListResponseDto;
import com.team573.gongguri.domain.groupPurchase.entity.PurchaseFilter;
import com.team573.gongguri.domain.groupPurchase.service.GroupPurchaseService;
import com.team573.gongguri.domain.myPage.service.MyPageService;
import com.team573.gongguri.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/my-page")
public class MyPageViewController {
    private final MyPageService myPageService;
    private final GroupPurchaseService groupPurchaseService;

    @GetMapping("")
    public String showMyPageForm(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        model.addAttribute("nickname", userDetails.getNickname());
        return "/myPage/main";
    }

    @GetMapping("/profile")
    public String showMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "ALL") PurchaseFilter status,
            Model model) {

        model.addAttribute("nickname", userDetails.getNickname());
        Long memberId = userDetails.getMemberId();

        // 내 작성 공동구매 리스트 조회
        List<GroupPurchaseListResponseDto> createdList = groupPurchaseService.findCreatedPurchases(memberId, status);

        // 뷰에 상태와 리스트 전달
        model.addAttribute("status", status.name());
        model.addAttribute("createdList", createdList);

        return "myPage/profile";
    }
    @GetMapping("/purchase")
    public String showMyPurchase(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {

        Long memberId = userDetails.getMemberId();

        List<GroupPurchaseListResponseDto> participatedList =
                myPageService.findMyParticipatedPurchases(memberId);

        model.addAttribute("participatedList", participatedList);
        return "myPage/purchase";
    }
}
