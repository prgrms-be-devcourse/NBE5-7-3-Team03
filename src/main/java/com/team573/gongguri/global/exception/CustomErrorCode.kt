package com.team573.gongguri.global.exception;


import static com.team573.gongguri.global.exception.ErrorStatus.BAD_REQUEST;
import static com.team573.gongguri.global.exception.ErrorStatus.CONFLICT;
import static com.team573.gongguri.global.exception.ErrorStatus.FORBIDDEN;
import static com.team573.gongguri.global.exception.ErrorStatus.NOT_FOUND;
import static com.team573.gongguri.global.exception.ErrorStatus.UNAUTHORIZED;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CustomErrorCode {

    NOT_FOUND_EXAMPLE(NOT_FOUND, "EXAMPLE-001", "존재하지 않는 예시입니다."),

    //GROUP_PURCHASE
    NOT_FOUND_GROUP_PURCHASE(NOT_FOUND, "POST-001", "해당 공동구매 게시글이 존재하지 않습니다."),
    CREATE_FAILED_GROUP_PURCHASE(CONFLICT, "POST-002", "공동구매 게시글 생성에 실패했습니다."),
    UPDATE_FAILED_GROUP_PURCHASE(CONFLICT, "POST-003", "공동구매 게시글 수정에 실패했습니다."),
    DELETE_FAILED_GROUP_PURCHASE(CONFLICT, "POST-004", "공동구매 게시글 삭제에 실패했습니다."),
    UNAUTHORIZED_GROUP_PURCHASE_ACCESS(FORBIDDEN, "POST-005", "해당 공동구매에 접근 권한이 없습니다."),
    UNAUTHORIZED_GROUP_PURCHASE_MANAGE(FORBIDDEN, "POST-006", "해당 공동구매에 대한 관리 권한이 없습니다."),
    FAILED_GROUP_PURCHASE_LIST(CONFLICT, "POST-007", "공동구매 게시글 목록 불러오기게 실패했습니다."),
    ALREADY_DELETE_GROUP_PURCHASE(CONFLICT, "POST-008" , "이미 삭제된 공동구매 게시글입니다."),
    DELETE_FAILED_WITH_DEPOSITED_PARTICIPANTS(CONFLICT, "POST-009", "입금 확인된 참여자가 있어 공동구매 게시글 삭제에 실패했습니다."),

    //GROUP_PURCHASE_PARTICIPATE
    ALREADY_JOINED(CONFLICT, "PARTICIPATE-001", "이미 공동구매에 참여하였습니다."),
    JOIN_FAILED(CONFLICT, "PARTICIPANT-002", "공동구매 참여에 실패했습니다."),
    PARTICIPANT_LIMIT_REACHED(BAD_REQUEST, "PARTICIPANT-003", "모집 인원이 모두 찼습니다."),
    NOT_FOUND_PARTICIPANT(NOT_FOUND, "PARTICIPANT-004", "존재하지 않는 공동 구매 참가자입니다."),
    CANNOT_CANCEL_PAID_PARTICIPANT(FORBIDDEN, "PARTICIPANT-005", "이미 입금한 사용자는 강퇴할 수 없습니다."),
    IS_NOT_COMPLETED(BAD_REQUEST, "PARTICIPANT-006", "완료된 공동 구매가 아닙니다."),

    // COMMON
    INVALID_REQUEST(BAD_REQUEST, "COMMON-001", "잘못된 요청입니다."),

    // AUTH
    FAILED_AUTHENTICATION(UNAUTHORIZED, "AUTH-01", "사용자 인증에 실패했습니다."),
    LOGIN_FAILED(UNAUTHORIZED, "AUTH-02", "아이디 또는 비밀번호가 올바르지 않습니다."),

    // MEMBER
    NOT_FOUND_MEMBER(NOT_FOUND, "MEMBER-001", "존재하지 않는 회원입니다."),
    EMAIL_ALREADY_EXISTS(CONFLICT, "MEMBER-002", "이미 사용 중인 이메일입니다."),
    NICKNAME_ALREADY_EXISTS(CONFLICT, "MEMBER-003", "이미 사용 중인 닉네임입니다."),
    EMAIL_NOT_VERIFIED(BAD_REQUEST, "MEMBER-004", "이메일 인증이 완료되지 않았습니다."),

    // CHAT
    NOT_FOUND_CHATROOM(NOT_FOUND, "CHAT-001", "존재하지 않는 채팅방입니다."),
    NOT_PARTICIPATING(FORBIDDEN, "CHAT-002", "채팅방에 참여하고 있지 않습니다."),
    CREATE_FAILED_CHATROOM(CONFLICT, "CHAT-003", "생성에 실패했습니다."),
    CHAT_JOIN_FALED(CONFLICT,"CHAT-004", "채팅방 참여에 실패했습니다."),

    //UNIV
    NOT_FOUND_UNIV(NOT_FOUND, "UNIV-001", "해당 대학교 정보가 존재하지 않습니다."),

    //IMAGE_UPLOAD
    INVALID_IMAGE_FILE(BAD_REQUEST, "IMAGE-001", "잘못된 이미지 파일입니다."),
    IMAGE_UPLOAD_FAILED(CONFLICT, "IMAGE-002", "이미지 업로드에 실패했습니다."), ;

    private final ErrorStatus errorStatus;
    private final String code;
    private final String message;

    public HttpStatus getStatus() {
        return this.errorStatus.getHttpStatus();
    }
}
