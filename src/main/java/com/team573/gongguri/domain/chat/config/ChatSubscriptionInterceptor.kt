package com.team573.gongguri.domain.chat.config;

import static com.team573.gongguri.global.exception.CustomErrorCode.FAILED_AUTHENTICATION;
import static com.team573.gongguri.global.exception.CustomErrorCode.INVALID_REQUEST;
import static com.team573.gongguri.global.exception.CustomErrorCode.NOT_FOUND_MEMBER;
import static com.team573.gongguri.global.exception.CustomErrorCode.NOT_FOUND_CHATROOM;

import com.team573.gongguri.domain.chat.entity.ChatRoom;
import com.team573.gongguri.domain.chat.repository.ChatRoomParticipationRepository;
import com.team573.gongguri.domain.chat.repository.ChatRoomRepository;
import com.team573.gongguri.domain.member.entity.Member;
import com.team573.gongguri.domain.member.repository.MemberRepository;
import com.team573.gongguri.global.exception.CustomException;
import com.team573.gongguri.global.security.CustomUserDetails;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatSubscriptionInterceptor implements ChannelInterceptor {

    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomParticipationRepository chatRoomParticipationRepository;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        log.info("채팅방 검증 시작");

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        // accessor null 체크
        if (accessor == null || accessor.getCommand() == null) {
            log.error(INVALID_REQUEST.getMessage());
            throw new CustomException(INVALID_REQUEST);
        }

        // 구독이 아닌 경우 검증 건너뜀
        if (!StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            return message;
        }

        log.info("구독 검증 시작");

        Authentication authentication = (Authentication) accessor.getUser();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.error(FAILED_AUTHENTICATION.getMessage());
            throw new CustomException(FAILED_AUTHENTICATION);
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long memberId = userDetails.getMemberId();
        String destination = accessor.getDestination();
        Long roomId = extractRoomId(destination);

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER));
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
            .orElseThrow(() -> new CustomException(NOT_FOUND_CHATROOM));

        if (!chatRoomParticipationRepository.existsByChatRoomAndMember(chatRoom, member)) {
            log.error(NOT_FOUND_MEMBER.getMessage());
            throw new CustomException(NOT_FOUND_MEMBER);
        }

        return message;
    }

    // "/room/{roomId}" 형식의 문자열에서 roomId 추출
    private Long extractRoomId(String destination) {
        String prefix = "/room/";
        if (destination != null && destination.startsWith(prefix)) {
            String roomIdStr = destination.substring(prefix.length());
            return Long.parseLong(roomIdStr);
        } else {
            log.error(NOT_FOUND_MEMBER.getMessage());
            throw new CustomException(NOT_FOUND_MEMBER);
        }
    }

}
