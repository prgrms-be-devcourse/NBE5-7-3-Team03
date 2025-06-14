package com.team573.gongguri.domain.member.service

import com.team573.gongguri.domain.member.dto.JoinRequestDto
import com.team573.gongguri.domain.member.dto.LikeInfoDto
import com.team573.gongguri.domain.member.entity.Member
import com.team573.gongguri.domain.member.entity.Univ
import com.team573.gongguri.domain.member.repository.MemberRepository
import com.team573.gongguri.domain.member.repository.UnivRepository
import com.team573.gongguri.global.exception.CustomErrorCode
import com.team573.gongguri.global.exception.CustomException
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.password.PasswordEncoder

class MemberServiceTests {

    private val memberRepository = mockk<MemberRepository>()
    private val univRepository = mockk<UnivRepository>()
    private val passwordEncoder = mockk<PasswordEncoder>()

    private val service = MemberService(memberRepository, univRepository, passwordEncoder)

    @Nested
    @DisplayName("join 은")
    inner class JoinTests {

        @Test
        fun `정상적으로 회원가입이 된다`() {
            // given
            val dto = JoinRequestDto(
                "test@test.com",
                "닉네임",
                "pw",
                "대학교",
                verified = true,
            )
            every { memberRepository.existsByEmail(any()) } returns false
            every { memberRepository.existsByNickname(any()) } returns false
            every { univRepository.findByUnivName("대학교") } returns null
            every { univRepository.save(any()) } returns Univ("대학교")
            every { passwordEncoder.encode("pw") } returns "encodedPw"
            every { memberRepository.save(any()) } returns mockk()

            // when
            service.join(dto)

            // then
            verify { memberRepository.save(any()) }
        }

        @Test
        fun `중복된 이메일이 있으면 예외를 던진다`() {
            // given
            val dto = JoinRequestDto(
                "dup@test.com",
                "닉네임",
                "pw",
                "대학교",
                verified = true)

            every { memberRepository.existsByEmail(dto.email) } returns true
            every { memberRepository.existsByNickname(dto.nickname) } returns false

            // when
            val exception = assertThrows<CustomException> {
                service.join(dto)
            }

            // then
            exception.getCustomErrorCode() shouldBe CustomErrorCode.EMAIL_ALREADY_EXISTS
        }
        @Test
        fun `중복된 닉네임이 있으면 예외를 던진다`() {
            // given
            val dto = JoinRequestDto(
                "test@test.com",
                "dupNick",
                "pw",
                "대학교",
                verified = true)

            every { memberRepository.existsByEmail(dto.email) } returns false
            every { memberRepository.existsByNickname(dto.nickname) } returns true

            // when
            val exception = assertThrows<CustomException> {
                service.join(dto)
            }

            // then
            exception.getCustomErrorCode() shouldBe CustomErrorCode.NICKNAME_ALREADY_EXISTS
        }

        @Test
        fun `이메일 인증되지 않으면 예외를 던진다`() {
            // given
            val dto = JoinRequestDto(
                "test@test.com",
                "닉네임",
                "pw",
                "대학교",
                verified = false
            )

            // when
            val exception = assertThrows<CustomException> {
                service.join(dto)
            }

            // then
            exception.getCustomErrorCode() shouldBe CustomErrorCode.EMAIL_NOT_VERIFIED
        }
    }

    @Nested
    @DisplayName("validateLoginError 는")
    inner class ValidateLoginErrorTests {

        @Test
        fun `유효하지 않은 에러 문자열이면 INVALID_REQUEST 예외를 던진다`() {
            // when
            val exception = assertThrows<CustomException> {
                service.validateLoginError("SOME_UNKNOWN_ERROR")
            }

            // then
            exception.getCustomErrorCode() shouldBe CustomErrorCode.INVALID_REQUEST
        }

        @Test
        fun `유효한 에러 코드 문자열이면 그에 맞는 예외를 던진다`() {
            // when
            val exception = assertThrows<CustomException> {
                service.validateLoginError("EMAIL_ALREADY_EXISTS")
            }

            // then
            exception.getCustomErrorCode() shouldBe CustomErrorCode.EMAIL_ALREADY_EXISTS
        }

        @Test
        fun `예외가 없으면 아무 것도 하지 않는다`() {
            // then
            service.validateLoginError(null)
        }
    }

    @Nested
    @DisplayName("getMemberById 는")
    inner class GetMemberByIdTests {

        @Test
        fun `존재하지 않는 멤버면 예외를 던진다`() {
            // given
            every { memberRepository.findByMemberId(1L) } returns null

            // when
            val exception = assertThrows<CustomException> {
                service.getMemberById(1L)
            }

            // then
            exception.getCustomErrorCode() shouldBe CustomErrorCode.NOT_FOUND_MEMBER
        }

        @Test
        fun `존재하는 멤버면 반환한다`() {
            // given
            val member = mockk<Member>()
            every { memberRepository.findByMemberId(1L) } returns member

            // when
            val result = service.getMemberById(1L)

            // then
            result shouldBe member
        }
    }

    @Nested
    @DisplayName("getLikeInfo 는")
    inner class GetLikeInfoTests {

        @Test
        fun `존재하는 멤버의 좋아요 정보를 반환한다`() {
            // given
            val member = mockk<Member> {
                every { likeCount } returns 10
                every { dislikeCount } returns 3
            }

            every { memberRepository.findByMemberId(1L) } returns member

            // when
            val result = service.getLikeInfo(1L)

            // then
            result shouldBe LikeInfoDto(likeCount = 10, dislikeCount = 3)
        }
        @Test
        fun `존재하지 않는 멤버에 대해 예외를 던진다`() {
            // given
            val member = mockk<Member> {
                every { likeCount } returns 10
                every { dislikeCount } returns 3
            }

            every { memberRepository.findByMemberId(1L) } returns null

            // when
            val exception = assertThrows<CustomException> {
                service.getLikeInfo(1L)
            }

            // then
            exception.getCustomErrorCode() shouldBe CustomErrorCode.NOT_FOUND_MEMBER
        }
    }
}
