package com.team573.gongguri.domain.member.controller;
import com.team573.gongguri.domain.member.dto.JoinRequestDto;
import com.team573.gongguri.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberViewController {
    private final MemberService memberService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error) {
        memberService.validateLoginError(error);
        return "member/login";
    }

    @GetMapping("/join")
    public String showJoinForm() {return "/member/join";}

    @GetMapping("/")
    public String showIndexForm() {return "redirect:/group-purchase";}


    @PostMapping("/join")
    public String join(@ModelAttribute JoinRequestDto joinRequest) {
        memberService.join(joinRequest);
        return "redirect:/login";
    }
}