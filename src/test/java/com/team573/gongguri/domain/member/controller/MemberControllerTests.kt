package com.team573.gongguri.domain.member.controller

import com.team573.gongguri.domain.member.repository.MemberRepository
import com.team573.gongguri.global.exception.CustomErrorCode
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@WebMvcTest(MemberController::class)
class MemberControllerTests {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var memberRepository: MemberRepository

    @Autowired
    lateinit var om: ObjectMapper

    @Test
    @WithMockUser("user1")
    fun `checkEmail은 중복된 이메일일 경우 에러 메시지를 반환한다`() {
        // given
        val email = "test@test.com"

        // when
        `when`(memberRepository.existsByEmail(email)).thenReturn(true)

        // then
        mockMvc.get("/api/member/check-email") {
            param("email", email)
        }.andExpect {
            status { isOk() }
            jsonPath("$.data.exists") { value(true) }
            jsonPath("$.msg") { value(CustomErrorCode.EMAIL_ALREADY_EXISTS.getMessage()) }
            jsonPath("$.code") { value(CustomErrorCode.EMAIL_ALREADY_EXISTS.getCode()) }
            jsonPath("$.status") { value(CustomErrorCode.EMAIL_ALREADY_EXISTS.getHttpStatus().value()) }
        }
    }

    @Test
    @WithMockUser("user1")
    fun `checkEmail은 사용 가능한 이메일일 경우 성공 메시지를 반환한다`() {
        val email = "available@example.com"
        `when`(memberRepository.existsByEmail(email)).thenReturn(false)

        mockMvc.get("/api/member/check-email") {
            param("email", email)
        }.andExpect {
            status { isOk() }
            jsonPath("$.data.exists") { value(false) }
            jsonPath("$.msg") { value("사용 가능한 이메일 주소입니다.") }
            jsonPath("$.code") { value("SUCCESS") }
            jsonPath("$.status") { value(200) }
        }
    }

    @Test
    @WithMockUser("user1")
    fun `checkNickname은 중복된 닉네임일 경우 에러 메시지를 반환한다`() {
        val nickname = "tester"
        `when`(memberRepository.existsByNickname(nickname)).thenReturn(true)

        mockMvc.get("/api/member/check-nickname") {
            param("nickname", nickname)
        }.andExpect {
            status { isOk() }
            jsonPath("$.data.exists") { value(true) }
            jsonPath("$.msg") { value(CustomErrorCode.NICKNAME_ALREADY_EXISTS.getMessage()) }
            jsonPath("$.code") { value(CustomErrorCode.NICKNAME_ALREADY_EXISTS.getCode()) }
            jsonPath("$.status") { value(CustomErrorCode.NICKNAME_ALREADY_EXISTS.getHttpStatus().value()) }
        }
    }

    @Test
    @WithMockUser("user1")
    fun `checkNickname은 사용 가능한 닉네임일 경우 성공 메시지를 반환한다`() {
        val nickname = "unique_user"
        `when`(memberRepository.existsByNickname(nickname)).thenReturn(false)

        mockMvc.get("/api/member/check-nickname") {
            param("nickname", nickname)
        }.andExpect {
            status { isOk() }
            jsonPath("$.data.exists") { value(false) }
            jsonPath("$.msg") { value("사용 가능한 닉네임입니다.") }
            jsonPath("$.code") { value("SUCCESS") }
            jsonPath("$.status") { value(200) }
        }
    }
}
