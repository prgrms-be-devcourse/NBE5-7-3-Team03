package com.team573.gongguri.domain.member.service;

import com.team573.gongguri.domain.member.dto.JoinRequestDto;
import com.team573.gongguri.domain.member.entity.Member;
import com.team573.gongguri.domain.member.entity.Univ;
import com.team573.gongguri.domain.member.mapper.MemberMapper;
import com.team573.gongguri.domain.member.repository.MemberRepository;
import com.team573.gongguri.domain.member.repository.UnivRepository;
import com.team573.gongguri.global.exception.CustomErrorCode;
import com.team573.gongguri.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final UnivRepository univRepository;
    private final PasswordEncoder passwordEncoder;

    public void join(JoinRequestDto joinRequestDto) {
        // 검증
        if (joinRequestDto.verified() == null || !joinRequestDto.verified()) {
            throw new CustomException(CustomErrorCode.EMAIL_NOT_VERIFIED);
        }
        validateDuplicateMember(joinRequestDto.email(), joinRequestDto.nickname());

        Univ univ = univRepository.findByUnivName(joinRequestDto.univName())
                .orElseGet(() -> univRepository.save(new Univ(joinRequestDto.univName())));

        String encodedPassword = passwordEncoder.encode(joinRequestDto.password());

        Member member = MemberMapper.toEntity(joinRequestDto, encodedPassword, univ);

        memberRepository.save(member);
    }
    public void validateLoginError(String error) {
        if (error != null) {
            try {
                CustomErrorCode errorCode = CustomErrorCode.valueOf(error);
                throw new CustomException(errorCode);
            } catch (IllegalArgumentException e) {
                throw new CustomException(CustomErrorCode.INVALID_REQUEST);
            }
        }
    }

    private void validateDuplicateMember(String email, String nickname) {
        if (memberRepository.existsByEmail(email)) {
            throw new CustomException(CustomErrorCode.EMAIL_ALREADY_EXISTS);
        }

        if (memberRepository.existsByNickname(nickname)) {
            throw new CustomException(CustomErrorCode.NICKNAME_ALREADY_EXISTS);
        }
    }
}