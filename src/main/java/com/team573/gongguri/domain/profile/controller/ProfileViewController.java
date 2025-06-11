package com.team573.gongguri.domain.profile.controller;

import com.team573.gongguri.domain.grouppurchase.dto.GroupPurchaseListResponseDto;
import com.team573.gongguri.domain.grouppurchase.entity.PurchaseFilter;
import com.team573.gongguri.domain.grouppurchase.service.GroupPurchaseService;
import com.team573.gongguri.domain.member.dto.LikeInfoDto;
import com.team573.gongguri.domain.member.entity.Member;
import com.team573.gongguri.domain.member.repository.MemberRepository;
import com.team573.gongguri.domain.member.service.MemberService;
import com.team573.gongguri.global.exception.CustomErrorCode;
import com.team573.gongguri.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileViewController {
    private final GroupPurchaseService groupPurchaseService;
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    //특정 멤버 id로 멤버 프로필 조회
    @GetMapping("/{memberId}")
    public String showMyProfile(
            @PathVariable Long memberId,
            @RequestParam(defaultValue = "ALL") PurchaseFilter status,
            Model model) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_MEMBER));

        model.addAttribute("nickname", member.getNickname());
        model.addAttribute("memberId", memberId);

        // 유저 작성 공동구매 리스트 조회
        List<GroupPurchaseListResponseDto> createdList = groupPurchaseService.findCreatedPurchases(memberId, status);

        // 뷰에 상태와 리스트 전달
        LikeInfoDto likeInfo = memberService.getLikeInfo(memberId);
        model.addAttribute("likeCount", likeInfo.getLikeCount());
        model.addAttribute("dislikeCount", likeInfo.getDislikeCount());
        model.addAttribute("status", status.name());
        model.addAttribute("createdList", createdList);

        return "profile/profile";
    }
}
