package com.team573.gongguri.domain.review.controller;

import com.team573.gongguri.domain.grouppurchase.dto.GroupPurchaseSimpleResponseDto;
import com.team573.gongguri.domain.grouppurchase.service.GroupPurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class ReviewViewController {

    private final GroupPurchaseService groupPurchaseService;

    @GetMapping("/group-purchase/{groupPurchaseId}/review")
    public String review(
        @PathVariable Long groupPurchaseId,
        Model model
    ) {
        GroupPurchaseSimpleResponseDto simpleInfo
            = groupPurchaseService.getSimpleInfo(groupPurchaseId);
        model.addAttribute("simpleInfo", simpleInfo);
        return "review/review";
    }
}
