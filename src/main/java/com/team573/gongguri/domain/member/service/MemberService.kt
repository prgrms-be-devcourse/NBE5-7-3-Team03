package com.team573.gongguri.domain.member.service

import com.team573.gongguri.domain.member.dto.JoinRequestDto
import com.team573.gongguri.domain.member.dto.LikeInfoDto
import com.team573.gongguri.domain.member.entity.Member
import com.team573.gongguri.domain.member.entity.Univ
import com.team573.gongguri.domain.member.mapper.toEntity
import com.team573.gongguri.domain.member.repository.MemberRepository
import com.team573.gongguri.domain.member.repository.UnivRepository
import com.team573.gongguri.global.exception.CustomErrorCode
import com.team573.gongguri.global.exception.CustomException
import lombok.RequiredArgsConstructor
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.function.Supplier

@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val univRepository: UnivRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun join(joinRequestDto: JoinRequestDto) {
        // 이메일 인증 여부 확인
        if (!joinRequestDto.verified) {
            throw CustomException(CustomErrorCode.EMAIL_NOT_VERIFIED)
        }

        validateDuplicateMember(joinRequestDto.email, joinRequestDto.nickname)

        val univ = univRepository.findByUnivName(joinRequestDto.univName)
            ?: univRepository.save(Univ(joinRequestDto.univName))

        val encodedPassword = passwordEncoder.encode(joinRequestDto.password)

        val member = toEntity(joinRequestDto, encodedPassword, univ)
        memberRepository.save(member)
    }

    fun validateLoginError(error: String?) {
        if (error != null) {
            try {
                val errorCode = CustomErrorCode.valueOf(error)
                throw CustomException(errorCode)
            } catch (e: IllegalArgumentException) {
                throw CustomException(CustomErrorCode.INVALID_REQUEST)
            }
        }
    }

    private fun validateDuplicateMember(email: String, nickname: String) {
        if (memberRepository.existsByEmail(email)) {
            throw CustomException(CustomErrorCode.EMAIL_ALREADY_EXISTS)
        }
        if (memberRepository.existsByNickname(nickname)) {
            throw CustomException(CustomErrorCode.NICKNAME_ALREADY_EXISTS)
        }
    }

    fun getMemberById(memberId: Long): Member {
        return memberRepository.findByMemberId(memberId)
            ?: throw CustomException(CustomErrorCode.NOT_FOUND_MEMBER)
    }

    fun getLikeInfo(memberId: Long): LikeInfoDto {
        val member = getMemberById(memberId)
        return LikeInfoDto(member.likeCount, member.dislikeCount)
    }
}