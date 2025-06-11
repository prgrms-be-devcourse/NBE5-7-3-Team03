package com.team573.gongguri.domain.member.dto

data class JoinRequestDto(
    val email: String,
    val nickname: String,
    val password: String,
    val univName: String,
    val verified: Boolean
) 