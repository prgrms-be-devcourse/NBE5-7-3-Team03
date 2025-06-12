package com.team573.gongguri.domain.member.controller

import com.team573.gongguri.domain.member.repository.MemberRepository
import com.team573.gongguri.global.dto.GeneralApiResponse
import com.team573.gongguri.global.exception.CustomErrorCode
import lombok.RequiredArgsConstructor
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
class MemberController(
    private val memberRepository: MemberRepository
) {
    @GetMapping("/check-email")
    fun checkEmail(@RequestParam email: String): GeneralApiResponse<Map<String, Boolean>> {
        val exists = memberRepository.existsByEmail(email)

        val (msg, code, status) = if (exists) {
            Triple(
                CustomErrorCode.EMAIL_ALREADY_EXISTS.getMessage(),
                CustomErrorCode.EMAIL_ALREADY_EXISTS.getCode(),
                CustomErrorCode.EMAIL_ALREADY_EXISTS.getHttpStatus().value()
            )
        } else {
            Triple(
                "사용 가능한 이메일 주소입니다.",
                "SUCCESS",
                HttpStatus.OK.value()
            )
        }

        return GeneralApiResponse(
            data = mapOf("exists" to exists),
            msg = msg,
            code = code,
            status = status
        )
    }

    @GetMapping("/check-nickname")
    fun checkNickname(@RequestParam nickname: String): GeneralApiResponse<Map<String, Boolean>> {
        val exists = memberRepository.existsByNickname(nickname)

        val (msg, code, status) = if (exists) {
            Triple(
                CustomErrorCode.NICKNAME_ALREADY_EXISTS.getMessage(),
                CustomErrorCode.NICKNAME_ALREADY_EXISTS.getCode(),
                CustomErrorCode.NICKNAME_ALREADY_EXISTS.getHttpStatus().value()
            )
        } else {
            Triple(
                "사용 가능한 닉네임입니다.",
                "SUCCESS",
                HttpStatus.OK.value()
            )
        }

        return GeneralApiResponse(
            data = mapOf("exists" to exists),
            msg = msg,
            code = code,
            status = status
        )
    }
}