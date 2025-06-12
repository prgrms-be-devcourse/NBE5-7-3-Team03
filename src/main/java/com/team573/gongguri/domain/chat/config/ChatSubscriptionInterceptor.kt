package com.team573.gongguri.domain.chat.config

import com.team573.gongguri.domain.chat.repository.ChatRoomParticipationRepository
import com.team573.gongguri.domain.chat.repository.ChatRoomRepository
import com.team573.gongguri.domain.member.repository.MemberRepository
import com.team573.gongguri.global.exception.CustomErrorCode
import com.team573.gongguri.global.exception.CustomException
import com.team573.gongguri.global.security.CustomUserDetails
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
class ChatSubscriptionInterceptor(
    private val memberRepository: MemberRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val chatRoomParticipationRepository: ChatRoomParticipationRepository
) : ChannelInterceptor {

    val log: Logger = LoggerFactory.getLogger(ChatSubscriptionInterceptor::class.java)

    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*> {
        log.info("채팅방 검증 시작")

        val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)

        // accessor null 체크
        if (accessor == null || accessor.command == null) {
            log.error(CustomErrorCode.INVALID_REQUEST.getMessage())
            throw CustomException(CustomErrorCode.INVALID_REQUEST)
        }

        // 구독이 아닌 경우 검증 건너뜀
        if (StompCommand.SUBSCRIBE != accessor.command) {
            return message
        }

        log.info("구독 검증 시작")

        val authentication = accessor.user as Authentication
        if (!authentication.isAuthenticated) {
            log.error(CustomErrorCode.FAILED_AUTHENTICATION.getMessage())
            throw CustomException(CustomErrorCode.FAILED_AUTHENTICATION)
        }

        val userDetails = authentication.principal as CustomUserDetails
        val memberId = userDetails.memberId
        val destination = accessor.destination
        val roomId = extractRoomId(destination)

        val member = memberRepository.findByIdOrNull(memberId) ?: throw CustomException(CustomErrorCode.NOT_FOUND_MEMBER)
        val chatRoom = chatRoomRepository.findByIdOrNull(roomId) ?: throw CustomException(CustomErrorCode.NOT_FOUND_CHATROOM)

        if (!chatRoomParticipationRepository.existsByChatRoomAndMember(chatRoom, member)) {
            log.error(CustomErrorCode.NOT_FOUND_MEMBER.getMessage())
            throw CustomException(CustomErrorCode.NOT_FOUND_MEMBER)
        }

        return message
    }

    // "/room/{roomId}" 형식의 문자열에서 roomId 추출
    private fun extractRoomId(destination: String?): Long {
        val prefix = "/room/"
        if (destination != null && destination.startsWith(prefix)) {
            val roomIdStr = destination.substring(prefix.length)
            return roomIdStr.toLong()
        } else {
            log.error(CustomErrorCode.NOT_FOUND_MEMBER.getMessage())
            throw CustomException(CustomErrorCode.NOT_FOUND_MEMBER)
        }
    }
}
