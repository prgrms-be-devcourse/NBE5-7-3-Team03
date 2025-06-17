package com.team573.gongguri.domain.member.intergration

import com.team573.gongguri.domain.member.entity.Member
import com.team573.gongguri.domain.member.entity.Univ
import com.team573.gongguri.domain.member.repository.UnivRepository
import com.team573.gongguri.global.exception.CustomErrorCode
import com.team573.gongguri.global.exception.CustomException
import com.team573.gongguri.integration.AbstractIntegrationTest
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.post
import org.springframework.http.MediaType

class MemberIntegrationTest: AbstractIntegrationTest() {
    private lateinit var setUpEmail: String
    private lateinit var setUpNickname: String
    private lateinit var setUpPassword: String
    private lateinit var setUpUnivName: String

    @Autowired
    private lateinit var univRepository: UnivRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @BeforeEach
    fun memberSetUp() {
        // given
        setUpEmail = "test1@email.com"
        setUpNickname = "닉네임"
        setUpPassword = "1234"
        setUpUnivName = "대학교"

        val univ = univRepository.findByUnivName(setUpUnivName) ?: univRepository.save(Univ(setUpUnivName))
        val encoded = passwordEncoder.encode(setUpPassword)
        val saved = Member(
            email = setUpEmail,
            nickname = setUpNickname,
            password = encoded,
            univ = univ,
        )
        memberRepository.save(saved)
    }

    @Nested
    @DisplayName("join 통합 테스트")
    inner class JoinTest {

        @Test
        fun `정상적으로 회원가입 되면 로그인 페이지로 리다이렉트`() {
            // given
            val email = "test2@test.com"
            val nickname = "닉네임2"
            val password = "1234"
            val univName = "대학교"

            // when & then
            mockMvc.post("/join") {
                contentType = MediaType.APPLICATION_FORM_URLENCODED
                param("email", email)
                param("nickname", nickname)
                param("password", password)
                param("univName", univName)
                param("verified", "true")
            }.andExpect {
                status { is3xxRedirection() }
                redirectedUrl("/login")
            }

            val saved = memberRepository.findByEmail(email).get()
            saved.nickname shouldBe nickname
            passwordEncoder.matches(password, saved.password) shouldBe true
            saved.univ.univName shouldBe univName
        }

        @Test
        fun `중복된 이메일이면 예외 발생`() {
            // when & then
            val result = mockMvc.post("/join") {
                contentType = MediaType.APPLICATION_FORM_URLENCODED
                param("email", setUpEmail)
                param("nickname", "다른닉네임")
                param("password", setUpPassword)
                param("univName", setUpUnivName)
                param("verified", "true")
            }.andReturn()

            val resolved = result.resolvedException as CustomException
            resolved.getCustomErrorCode() shouldBe CustomErrorCode.EMAIL_ALREADY_EXISTS
        }

        @Test
        fun `중복된 닉네임이면 예외 발생`() {
            // when & then
            val result = mockMvc.post("/join") {
                contentType = MediaType.APPLICATION_FORM_URLENCODED
                param("email", "another@test.com")
                param("nickname", setUpNickname)
                param("password", setUpPassword)
                param("univName", setUpUnivName)
                param("verified", "true")
            }.andReturn()

            val resolved = result.resolvedException as CustomException
            resolved.getCustomErrorCode() shouldBe CustomErrorCode.NICKNAME_ALREADY_EXISTS
        }

    }
    @Nested
    @DisplayName("login 통합 테스트")
    inner class loginTest {
        @Test
        fun `정상적으로 로그인에 성공한다`() {
            // when
            mockMvc.post("/login") {
                contentType = MediaType.APPLICATION_FORM_URLENCODED
                param("email", setUpEmail)
                param("password", setUpPassword)
            }.andExpect {
                status { is3xxRedirection() }
                redirectedUrl("/group-purchase")
            }
        }
        @Test
        fun `비밀번호가 틀리면 로그인에 실패한다`() {
            // when
            mockMvc.post("/login") {
                contentType = MediaType.APPLICATION_FORM_URLENCODED
                param("email", setUpEmail)
                param("password", "worngpassword")
            }.andExpect {
                status { is3xxRedirection() }
                redirectedUrl("/login?error=LOGIN_FAILED")
            }
        }
    }
}