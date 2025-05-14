package com.team573.gongguri.domain.chat.config;

import static com.team573.gongguri.global.exception.ErrorCode.FAILED_AUTHENTICATION;
import static com.team573.gongguri.global.exception.ErrorCode.INVALID_REQUEST;
import static com.team573.gongguri.global.exception.ErrorCode.NOT_FOUND_MEMBER;
import static com.team573.gongguri.global.exception.ErrorCode.NOT_FOUND_ROOM;

import com.team573.gongguri.domain.chat.entity.ChatRoom;
import com.team573.gongguri.domain.chat.repository.ChatRoomParticipationRepository;
import com.team573.gongguri.domain.chat.repository.ChatRoomRepository;
import com.team573.gongguri.domain.member.entity.Member;
import com.team573.gongguri.domain.member.repository.MemberRepository;
import com.team573.gongguri.global.exception.ErrorException;
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
            throw new ErrorException(INVALID_REQUEST);
        }

        // 구독 메시지인 경우만 처리
        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            log.info("구독 검증 시작");

            // 현재 인증된 사용자 정보 가져오기
            Authentication authentication = (Authentication) accessor.getUser();

            if (authentication == null) {
                throw new ErrorException(FAILED_AUTHENTICATION);
            }

            if (authentication.isAuthenticated()) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

                // 이메일 추출
                String email = userDetails.getUsername();

                // "/room/{roomId}" 형식에서 roomId 추출
                String destination = accessor.getDestination();
                Long roomId = this.extractRoomId(destination);

                // 회원, 채팅방 불러오기
                Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new ErrorException(NOT_FOUND_MEMBER));
                ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                    .orElseThrow(() -> new ErrorException(NOT_FOUND_ROOM));

                if (!chatRoomParticipationRepository.existsByChatRoomAndMember(chatRoom, member)) {
                    throw new ErrorException(NOT_FOUND_MEMBER);
                }
            }
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
            throw new ErrorException(NOT_FOUND_MEMBER);
        }
    }

}
