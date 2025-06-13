package com.team573.gongguri.domain.member.controller

import com.team573.gongguri.domain.member.service.MemberService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ModelAttribute
import com.team573.gongguri.domain.member.dto.JoinRequestDto
import org.springframework.stereotype.Controller

@Controller
class MemberViewController(
    private val memberService: MemberService
) {
    @GetMapping("/login")
    fun loginPage(@RequestParam("error", required = false) error: String?): String {
        memberService.validateLoginError(error)
        return "member/login"
    }

    @GetMapping("/join")
    fun showJoinForm(): String {
        return "member/join"
    }

    @GetMapping("/")
    fun showIndexForm(): String {
        return "redirect:/group-purchase"
    }

    @PostMapping("/join")
    fun join(@ModelAttribute joinRequest: JoinRequestDto): String {
        memberService.join(joinRequest)
        return "redirect:/login"
    }
}