package com.team573.gongguri.domain.myPage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/my-page")
public class MyPageViewController {

    @GetMapping("")
    public String showMyPageForm() {
        return "/my-page/main";
    }

    @GetMapping("/profile")
    public String showMyProfileForm() {
        return "/my-page/profile";
    }

    @GetMapping("/purchase")
    public String showMyPurchaseForm() {
        return "/my-page/purchase";
    }

}
