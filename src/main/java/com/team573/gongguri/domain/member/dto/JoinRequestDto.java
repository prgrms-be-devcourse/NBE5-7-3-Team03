package com.team573.gongguri.domain.member.dto;

public record JoinRequestDto(
        String email,
        String nickname,
        String password,
        String univName,
        Boolean verified
) {}