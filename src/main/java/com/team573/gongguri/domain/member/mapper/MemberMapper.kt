package com.team573.gongguri.domain.member.mapper

import com.team573.gongguri.domain.member.dto.JoinRequestDto
import com.team573.gongguri.domain.member.entity.Member
import com.team573.gongguri.domain.member.entity.Univ

fun toEntity(dto: JoinRequestDto, encodedPassword: String, univ: Univ): Member {
    return Member(
        email = dto.email,
        nickname = dto.nickname,
        password = encodedPassword,
        univ = univ
    )
}