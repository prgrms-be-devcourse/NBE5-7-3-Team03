package com.team573.gongguri.domain.groupPurchase.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/groupPurchase")
public class GroupPurchaseViewController {

    // 공동구매 목록 페이지
    @GetMapping("")
    public String showHomePage() {
        return "groupPurchase/group-purchase";

    }
    // 공동구매 게시글 작성 페이지
    @GetMapping("/post")
    public String showPostPage() {
        return "groupPurchase/group-purchase-post";
    }

    // 공동구매 게시글 상세 페이지
    @GetMapping("/{id}")
    public String showReadPage(@PathVariable Long id, Model model) {
        model.addAttribute("groupId", id);
        return "groupPurchase/group-purchase-read";
    }

    @GetMapping("/chats")
    public String showChats() {
        return "groupPurchase/group-purchase-chats";
    }
}
