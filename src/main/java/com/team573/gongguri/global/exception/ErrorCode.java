package com.team573.gongguri.global.exception;

import static com.team573.gongguri.global.exception.ErrorStatus.*;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    NOT_FOUND_EXAMPLE(NOT_FOUND, "EXAMPLE-001", "존재하지 않는 예시입니다."),

    // COMMON
    INVALID_REQUEST(BAD_REQUEST, "COMMON-001", "잘못된 요청입니다."),

    // AUTH
    FAILED_AUTHENTICATION(UNAUTHORIZED, "AUTH-01", "사용자 인증에 실패했습니다."),

    // MEMBER
    NOT_FOUND_MEMBER(NOT_FOUND, "MEMBER-001", "존재하지 않는 회원입니다."),

    // CHAT
    NOT_FOUND_ROOM(NOT_FOUND, "CHAT-001", "존재하지 않는 채팅방입니다."),
    NOT_PARTICIPATING(FORBIDDEN, "CHAT-002", "채팅방에 참여하고 있지 않습니다."),
    ;


    private final ErrorStatus errorStatus;
    private final String code;
    private final String message;
}
