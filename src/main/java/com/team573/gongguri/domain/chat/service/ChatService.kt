package com.team573.gongguri.domain.chat.service

import com.team573.gongguri.domain.chat.dto.ChatMessageRequestDto
import com.team573.gongguri.domain.chat.dto.ChatMessageResponseDto
import com.team573.gongguri.domain.chat.entity.ChatMessage
import com.team573.gongguri.domain.chat.entity.ChatRoom
import com.team573.gongguri.domain.chat.mapper.toChatMessage
import com.team573.gongguri.domain.chat.mapper.toDto
import com.team573.gongguri.domain.chat.mapper.toParticipationEntity
import com.team573.gongguri.domain.chat.repository.ChatMessageRepository
import com.team573.gongguri.domain.chat.repository.ChatRoomParticipationRepository
import com.team573.gongguri.domain.chat.repository.ChatRoomRepository
import com.team573.gongguri.domain.chat.repository.CustomChatMessageRepository
import com.team573.gongguri.domain.member.repository.MemberRepository
import com.team573.gongguri.global.exception.CustomErrorCode
import com.team573.gongguri.global.exception.CustomException
import org.bson.types.ObjectId
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ChatService(
    private val memberRepository: MemberRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val chatMessageRepository: ChatMessageRepository,
    private val chatRoomParticipationRepository: ChatRoomParticipationRepository,
    private val customChatMessageRepository: CustomChatMessageRepository,
) {


    // 채팅 메세지 저장
    fun addChatMessage(
        roomId: Long,
        requestDto: ChatMessageRequestDto
    ): ChatMessageResponseDto {
        val createdMessage = toChatMessage(roomId, requestDto.nickname, requestDto.content)
        val saved = chatMessageRepository.save(createdMessage)
        return toDto(saved)
    }

    // 채팅방 생성
    fun addChatRoom(email: String): ChatRoom {
        val chatRoom = chatRoomRepository.save(ChatRoom())
        addChatParticipation(chatRoom.chatRoomId!!, email)
        return chatRoom
    }

    // 채팅방 참여자 추가
    fun addChatParticipation(roomId: Long, email: String?) {
        //TODO findByIdOrNull로 바꾸기
        val member = memberRepository.findByEmail(email)
            .orElseThrow { CustomException(CustomErrorCode.NOT_FOUND_MEMBER) }

        val chatRoom = chatRoomRepository.findByIdOrNull(roomId) ?: throw CustomException(CustomErrorCode.NOT_FOUND_CHATROOM)

        val createdParticipation = toParticipationEntity(member, chatRoom)

        chatRoomParticipationRepository.save(createdParticipation)
    }

    // 채팅방 참여자 제거
    fun deleteChatParticipation(roomId: Long, memberId: Long) {
        val member = memberRepository.findByIdOrNull(memberId) ?: throw CustomException(CustomErrorCode.NOT_FOUND_MEMBER)
        val chatRoom = chatRoomRepository.findByIdOrNull(roomId) ?: throw CustomException(CustomErrorCode.NOT_FOUND_CHATROOM)

        chatRoomParticipationRepository.deleteByChatRoomAndMember(chatRoom, member)
    }

    // 채팅방 찾기
    fun getChatRoomIdByGroupPurchaseId(groupPurchaseId: Long): Long? {
        val findChatRoom = chatRoomRepository.findChatRoomByGroupId(groupPurchaseId)
        return findChatRoom.chatRoomId
    }

    // 이전 채팅 메시지 조회
    fun getMessages(roomId: Long, cursor: String?, size: Int): List<ChatMessageResponseDto> {
        val pageRequest = PageRequest.of(0, size)
        val messages: List<ChatMessage>

        if (cursor.isNullOrBlank()) {
            // 최신 메시지 조회
            messages = chatMessageRepository.findLatestByRoomId(roomId, pageRequest)
        } else {
            // 커서 이전 메시지 조회
            val cursorId = ObjectId(cursor)
            messages = chatMessageRepository.findLatestByRoomIdAndCursor(roomId, cursorId, pageRequest)
        }

        return messages.stream()
            .map { chatMessage: ChatMessage -> toDto(chatMessage) }
            .toList()
    }

    // 가장 최근 메시지 조회
    fun getFirstMessageMap(chatRoomIds: List<Long>): Map<Long, String> {
        return customChatMessageRepository.findLatestMessageByRoomIds(chatRoomIds)
    }
}
