package com.team573.gongguri.global.exception

import org.springframework.http.HttpStatus


enum class CustomErrorCode(
    private val errorStatus: ErrorStatus,
    private val code: String,
    private val message: String
) {
    NOT_FOUND_EXAMPLE(ErrorStatus.NOT_FOUND, "EXAMPLE-001", "존재하지 않는 예시입니다."),

    //GROUP_PURCHASE
    NOT_FOUND_GROUP_PURCHASE(ErrorStatus.NOT_FOUND, "POST-001", "해당 공동구매 게시글이 존재하지 않습니다."),
    CREATE_FAILED_GROUP_PURCHASE(ErrorStatus.CONFLICT, "POST-002", "공동구매 게시글 생성에 실패했습니다."),
    UPDATE_FAILED_GROUP_PURCHASE(ErrorStatus.CONFLICT, "POST-003", "공동구매 게시글 수정에 실패했습니다."),
    DELETE_FAILED_GROUP_PURCHASE(ErrorStatus.CONFLICT, "POST-004", "공동구매 게시글 삭제에 실패했습니다."),
    UNAUTHORIZED_GROUP_PURCHASE_ACCESS(ErrorStatus.FORBIDDEN, "POST-005", "해당 공동구매에 접근 권한이 없습니다."),
    UNAUTHORIZED_GROUP_PURCHASE_MANAGE(ErrorStatus.FORBIDDEN, "POST-006", "해당 공동구매에 대한 관리 권한이 없습니다."),
    FAILED_GROUP_PURCHASE_LIST(ErrorStatus.CONFLICT, "POST-007", "공동구매 게시글 목록 불러오기게 실패했습니다."),
    ALREADY_DELETE_GROUP_PURCHASE(ErrorStatus.CONFLICT, "POST-008", "이미 삭제된 공동구매 게시글입니다."),
    DELETE_FAILED_WITH_DEPOSITED_PARTICIPANTS(ErrorStatus.CONFLICT, "POST-009", "입금 확인된 참여자가 있어 공동구매 게시글 삭제에 실패했습니다."),

    //GROUP_PURCHASE_PARTICIPATE
    ALREADY_JOINED(ErrorStatus.CONFLICT, "PARTICIPATE-001", "이미 공동구매에 참여하였습니다."),
    JOIN_FAILED(ErrorStatus.CONFLICT, "PARTICIPANT-002", "공동구매 참여에 실패했습니다."),
    PARTICIPANT_LIMIT_REACHED(ErrorStatus.BAD_REQUEST, "PARTICIPANT-003", "모집 인원이 모두 찼습니다."),
    NOT_FOUND_PARTICIPANT(ErrorStatus.NOT_FOUND, "PARTICIPANT-004", "존재하지 않는 공동 구매 참가자입니다."),
    CANNOT_CANCEL_PAID_PARTICIPANT(ErrorStatus.FORBIDDEN, "PARTICIPANT-005", "이미 입금한 사용자는 강퇴할 수 없습니다."),
    IS_NOT_COMPLETED(ErrorStatus.BAD_REQUEST, "PARTICIPANT-006", "완료된 공동 구매가 아닙니다."),

    // COMMON
    INVALID_REQUEST(ErrorStatus.BAD_REQUEST, "COMMON-001", "잘못된 요청입니다."),

    // AUTH
    FAILED_AUTHENTICATION(ErrorStatus.UNAUTHORIZED, "AUTH-01", "사용자 인증에 실패했습니다."),
    LOGIN_FAILED(ErrorStatus.UNAUTHORIZED, "AUTH-02", "아이디 또는 비밀번호가 올바르지 않습니다."),

    // MEMBER
    NOT_FOUND_MEMBER(ErrorStatus.NOT_FOUND, "MEMBER-001", "존재하지 않는 회원입니다."),
    EMAIL_ALREADY_EXISTS(ErrorStatus.CONFLICT, "MEMBER-002", "이미 사용 중인 이메일입니다."),
    NICKNAME_ALREADY_EXISTS(ErrorStatus.CONFLICT, "MEMBER-003", "이미 사용 중인 닉네임입니다."),
    EMAIL_NOT_VERIFIED(ErrorStatus.BAD_REQUEST, "MEMBER-004", "이메일 인증이 완료되지 않았습니다."),

    // VERIF
    VERIFICATION_SEND_FAILED(ErrorStatus.INTERNAL_SERVER_ERROR,"VERIF_001", "인증 코드 발송에 실패했습니다."),
    VERIFICATION_CHECK_FAILED(ErrorStatus.INTERNAL_SERVER_ERROR, "VERIF_002", "이메일 인증 확인 중 오류가 발생했습니다."),
    VERIFICATION_CODE_MISMATCH(ErrorStatus.BAD_REQUEST, "VERIF_003", "인증 코드가 일치하지 않습니다.", ),
    VERIFICATION_SERVER_ERROR(ErrorStatus.INTERNAL_SERVER_ERROR, "VERIF_004", "인증 서버에 에러가 발생했습니다.", ),
    CERT_CLEAR_FAILED(ErrorStatus.INTERNAL_SERVER_ERROR, "VERIF_005", "인증 정보 초기화에 실패했습니다.", ),

    // CHAT
    NOT_FOUND_CHATROOM(ErrorStatus.NOT_FOUND, "CHAT-001", "존재하지 않는 채팅방입니다."),
    NOT_PARTICIPATING(ErrorStatus.FORBIDDEN, "CHAT-002", "채팅방에 참여하고 있지 않습니다."),
    CREATE_FAILED_CHATROOM(ErrorStatus.CONFLICT, "CHAT-003", "생성에 실패했습니다."),
    CHAT_JOIN_FALED(ErrorStatus.CONFLICT, "CHAT-004", "채팅방 참여에 실패했습니다."),

    //UNIV
    NOT_FOUND_UNIV(ErrorStatus.NOT_FOUND, "UNIV-001", "해당 대학교 정보가 존재하지 않습니다."),

    //IMAGE_UPLOAD
    INVALID_IMAGE_FILE(ErrorStatus.BAD_REQUEST, "IMAGE-001", "잘못된 이미지 파일입니다."),
    IMAGE_UPLOAD_FAILED(ErrorStatus.CONFLICT, "IMAGE-002", "이미지 업로드에 실패했습니다."),
    ;

    fun getHttpStatus(): HttpStatus = errorStatus.getHttpStatus()
    fun getMessage(): String = message
    fun getCode(): String = code
}
