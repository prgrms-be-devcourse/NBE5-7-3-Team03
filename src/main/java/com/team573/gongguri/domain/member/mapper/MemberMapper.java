package com.team573.gongguri.domain.member.mapper;

import com.team573.gongguri.domain.member.dto.JoinRequestDto;
import com.team573.gongguri.domain.member.entity.Member;
import com.team573.gongguri.domain.member.entity.Univ;

public class MemberMapper {

    public static Member toEntity(JoinRequestDto dto, String encodedPassword, Univ univ) {
        return Member.builder()
                .email(dto.getEmail())
                .nickname(dto.getNickname())
                .password(encodedPassword)
                .likeCount(0)
                .dislikeCount(0)
                .univ(univ)
                .build();
    }
}