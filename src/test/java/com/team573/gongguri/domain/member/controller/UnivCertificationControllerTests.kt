package com.team573.gongguri.domain.member.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.team573.gongguri.domain.member.service.UnivCertificationService
import com.team573.gongguri.global.exception.CustomErrorCode
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.io.IOException

@WebMvcTest(UnivCertificationController::class)
class UnivCertificationControllerTests {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var univCertificationService: UnivCertificationService

    @Autowired
    lateinit var om: ObjectMapper

    @Test
    @WithMockUser("user1")
    fun `sendVerificationCode는 정상 요청시 성공 응답을 반환한다`() {
        val email = "test@test.com"
        val univName = "대학교"

        mockMvc.get("/send-verification-code") {
            param("email", email)
            param("univName", univName)
        }.andExpect {
            status { isOk() }
            jsonPath("$.data.success") { value(true) }
            jsonPath("$.msg") { value("인증 코드가 성공적으로 발송되었습니다.") }
            jsonPath("$.code") { value("SUCCESS") }
            jsonPath("$.status") { value(200) }
        }
    }

    @Test
    @WithMockUser("user1")
    fun `sendVerificationCode에서 IOException이 발생하면 실패 응답을 반환한다`() {
        val email = "test@test.com"
        val univName = "대학교"

        `when`(univCertificationService.sendVerificationCode(email, univName)).thenThrow(IOException("발송 실패"))

        mockMvc.get("/send-verification-code") {
            param("email", email)
            param("univName", univName)
        }.andExpect {
            status { isOk() }
            jsonPath("$.data.success") { value(false) }
            jsonPath("$.msg") { value(CustomErrorCode.VERIFICATION_SEND_FAILED.getMessage()) }
            jsonPath("$.code") { value(CustomErrorCode.VERIFICATION_SEND_FAILED.getCode()) }
            jsonPath("$.status") { value(CustomErrorCode.VERIFICATION_SEND_FAILED.getHttpStatus().value()) }
        }
    }

    @Test
    @WithMockUser("user1")
    fun `verifyEmailCode는 인증 성공시 true를 반환한다`() {
        val email = "test@test.com"
        val univName = "대학교"
        val code = "1234"

        `when`(univCertificationService.verifyEmailCode(email, univName, code)).thenReturn(true)

        mockMvc.get("/verify-email-code") {
            param("email", email)
            param("univName", univName)
            param("verificationCode", code)
        }.andExpect {
            status { isOk() }
            jsonPath("$.data.success") { value(true) }
            jsonPath("$.msg") { value("이메일 인증에 성공했습니다.") }
            jsonPath("$.code") { value("SUCCESS") }
            jsonPath("$.status") { value(200) }
        }
    }

    @Test
    @WithMockUser("user1")
    fun `verifyEmailCode는 인증 실패시 false와 에러 메시지를 반환한다`() {
        val email = "test@test.com"
        val univName = "대학교"
        val code = "wrong"

        `when`(univCertificationService.verifyEmailCode(email, univName, code)).thenReturn(false)

        mockMvc.get("/verify-email-code") {
            param("email", email)
            param("univName", univName)
            param("verificationCode", code)
        }.andExpect {
            status { isOk() }
            jsonPath("$.data.success") { value(false) }
            jsonPath("$.msg") { value(CustomErrorCode.VERIFICATION_CODE_MISMATCH.getMessage()) }
            jsonPath("$.code") { value(CustomErrorCode.VERIFICATION_CODE_MISMATCH.getCode()) }
            jsonPath("$.status") { value(CustomErrorCode.VERIFICATION_CODE_MISMATCH.getHttpStatus().value()) }
        }
    }

    @Test
    @WithMockUser("user1")
    fun `verifyEmailCode에서 IOException 발생 시 에러 메시지를 반환한다`() {
        val email = "student@univ.ac.kr"
        val univName = "테스트대학교"
        val code = "error"

        `when`(univCertificationService.verifyEmailCode(email, univName, code)).thenThrow(IOException("예외 발생"))

        mockMvc.get("/verify-email-code") {
            param("email", email)
            param("univName", univName)
            param("verificationCode", code)
        }.andExpect {
            status { isOk() }
            jsonPath("$.data") { doesNotExist() }
            jsonPath("$.msg") { value(CustomErrorCode.VERIFICATION_CHECK_FAILED.getMessage()) }
            jsonPath("$.code") { value(CustomErrorCode.VERIFICATION_CHECK_FAILED.getCode()) }
            jsonPath("$.status") { value(CustomErrorCode.VERIFICATION_CHECK_FAILED.getHttpStatus().value()) }
        }
    }
}
