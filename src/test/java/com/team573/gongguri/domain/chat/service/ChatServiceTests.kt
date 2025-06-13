package com.team573.gongguri.domain.chat.service

import com.team573.gongguri.domain.chat.dto.ChatMessageRequestDto
import com.team573.gongguri.domain.chat.entity.ChatMessage
import com.team573.gongguri.domain.chat.repository.ChatMessageRepository
import com.team573.gongguri.domain.chat.repository.ChatRoomParticipationRepository
import com.team573.gongguri.domain.chat.repository.ChatRoomRepository
import com.team573.gongguri.domain.chat.repository.CustomChatMessageRepository
import com.team573.gongguri.domain.member.repository.MemberRepository
import com.team573.gongguri.global.exception.CustomErrorCode
import com.team573.gongguri.global.exception.CustomException
import com.team573.gongguri.util.ChatMessageUtil
import com.team573.gongguri.util.ChatParticipantUtil
import com.team573.gongguri.util.ChatRoomUtil
import com.team573.gongguri.util.MemberUtil
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.bson.types.ObjectId
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull

class ChatServiceTests {

    val memberRepository = mockk<MemberRepository>()
    val chatRoomRepository = mockk<ChatRoomRepository>()
    val chatMessageRepository = mockk<ChatMessageRepository>()
    val chatRoomParticipationRepository = mockk<ChatRoomParticipationRepository>()
    val customChatMessageRepository = mockk<CustomChatMessageRepository>()

    val service = ChatService(
     memberRepository,
     chatRoomRepository,
     chatMessageRepository,
     chatRoomParticipationRepository,
     customChatMessageRepository
    )



    @Nested
    @DisplayName("addChatMessage 는")
    inner class AddChatMessageTests {

        @Test
        fun `채팅 메시지를 저장하고 ChatMessageRequestDto를 반환한다`() {
            // given
            val roomId: Long = 1
            val requestDto = ChatMessageRequestDto(
                nickname = "test1",
                content = "test chat"
            )

            val createdChatMessage = ChatMessage(
                roomId = roomId,
                nickname = requestDto.nickname,
                content = requestDto.content
            )

            val savedChatMessage = ChatMessage(
                id = ObjectId(),
                roomId = roomId,
                nickname = requestDto.nickname,
                content = requestDto.content
            )

            every {chatMessageRepository.save(createdChatMessage)}.returns(savedChatMessage)

            // when
            val result = service.addChatMessage(roomId, requestDto)

            // then
            result.nickname shouldBe requestDto.nickname
            result.content shouldBe requestDto.content
        }
    }

    @Nested
    @DisplayName("addChatRoom 는")
    inner class AddChatRoomTests {
        @Test
        fun `채팅방을 추가하고 반환한다`() {
            // given
            val email = "test@test.com"
            val roomId: Long = 1

            val createdRoom = ChatRoomUtil.createWithId(roomId)
            val addMember = MemberUtil.createWithId(1)
            val createdParticipant = ChatParticipantUtil.createWithId(1, addMember, createdRoom)

            every { chatRoomRepository.save(any()) }.returns(createdRoom)
            every { memberRepository.findNullableByEmail(email) } returns addMember
            every { chatRoomRepository.findByIdOrNull(roomId) } returns createdRoom
            every { chatRoomParticipationRepository.save(any()) } returns createdParticipant

            // when
            val result = service.addChatRoom(email)

            // then
            result.chatRoomId shouldBe createdRoom.chatRoomId
        }
    }

    @Nested
    @DisplayName("addChatParticipation 는")
    inner class AddChatParticipationTests {
        @Test
        fun `회원과 채팅방이 존재하면 참여자로 등록한다`() {
            // given
            val email = "test@test.com"
            val roomId: Long = 1
            val memberId: Long = 1

            val createdRoom = ChatRoomUtil.createWithId(roomId)
            val addMember = MemberUtil.createWithId(memberId)
            val createdParticipant = ChatParticipantUtil.create(addMember, createdRoom)

            every { memberRepository.findNullableByEmail(email) } returns addMember
            every { chatRoomRepository.findByIdOrNull(roomId) } returns createdRoom
            every { chatRoomParticipationRepository.save(createdParticipant) } returns createdParticipant

            // when
            service.addChatParticipation(roomId, email)

            // then
            verify (exactly = 1) {chatRoomParticipationRepository.save(createdParticipant)}
        }

        @Test
        fun `회원이 존재하지 않으면 예외를 발생한다`() {
            // given
            val email = "test@test.com"
            val roomId: Long = 1

            every { memberRepository.findNullableByEmail(email) } returns null

            // when
            val exception = assertThrows<CustomException> { service.addChatParticipation(roomId, email) }

            // then
            exception.getCustomErrorCode() shouldBe CustomErrorCode.NOT_FOUND_MEMBER
        }

        @Test
        fun `채팅방이 존재하지 않으면 예외를 발생한다`() {
            // given
            val email = "test@test.com"
            val roomId: Long = 1
            val memberId: Long = 1

            val addMember = MemberUtil.createWithId(memberId)

            every { memberRepository.findNullableByEmail(email) } returns addMember
            every { chatRoomRepository.findByIdOrNull(roomId) } returns null

            // when
            val exception = assertThrows<CustomException> { service.addChatParticipation(roomId, email) }


            // then
            exception.getCustomErrorCode() shouldBe CustomErrorCode.NOT_FOUND_CHATROOM
        }
    }

    @Nested
    @DisplayName("deleteChatParticipation 는")
    inner class DeleteChatParticipationTests {

        @Test
        fun `회원과 채팅방이 존재하면 채팅방에서 참여자를 제거한다`() {
            // given
            val roomId: Long = 1
            val memberId: Long = 1

            val member = MemberUtil.createWithId(memberId)
            val chatRoom = ChatRoomUtil.createWithId(roomId)

            every { memberRepository.findByIdOrNull(memberId) }.returns(member)
            every { chatRoomRepository.findByIdOrNull(roomId) }.returns(chatRoom)
            every { chatRoomParticipationRepository.deleteByChatRoomAndMember(chatRoom, member) } just Runs

            // when
            service.deleteChatParticipation(roomId, memberId)

            // then
            verify(exactly = 1) { chatRoomParticipationRepository.deleteByChatRoomAndMember(chatRoom, member)  }
        }

        @Test
        fun `ID에 해당하는 회원이 존재하지 않으면 예외를 발생한다`() {
            // given
            val roomId: Long = 1
            val memberId: Long = 1

            every {memberRepository.findByIdOrNull(memberId) }.returns(null)

            // when
            val exception = assertThrows<CustomException> { service.deleteChatParticipation(roomId, memberId) }

            // then
            exception.getCustomErrorCode() shouldBe CustomErrorCode.NOT_FOUND_MEMBER
        }

        @Test
        fun `ID에 해당하는 채팅방이 존재하지 않으면 예외를 발생한다`() {
            // given
            val roomId: Long = 1
            val memberId: Long = 1

            val member = MemberUtil.createWithId(memberId)

            every {memberRepository.findByIdOrNull(memberId) }.returns(member)
            every { chatRoomRepository.findByIdOrNull(roomId) }.returns(null)

            // when
            val exception = assertThrows<CustomException> { service.deleteChatParticipation(roomId, memberId) }

            // then
            exception.getCustomErrorCode() shouldBe CustomErrorCode.NOT_FOUND_CHATROOM
        }

    }

    @Nested
    @DisplayName("getMessages 는")
    inner class GetMessagesTests {
        @Test
        fun `cursor가 null이면 크기가 size인 가장 최신 채팅 메시지 ChatMessageResponseDto 리스트를 반환한다`() {
            // given
            val chatMessageList = ChatMessageUtil.createList()

            val roomId: Long = 1
            val cursor: String? = null
            val size = 10

            val pageRequest = PageRequest.of(0, size)

            every{ chatMessageRepository.findLatestByRoomId(roomId, pageRequest) }.returns(chatMessageList)

            // when
            val result = service.getMessages(roomId, cursor, size)

            // then
            result.size shouldBe size
            for(i in 0 until size) {
                result[i].messageId shouldBe chatMessageList[i].id!!.toHexString()
            }
        }

        @Test
        fun `cursor가 null이 아니면 크기가 size인 cusor 이전의 ChatMessageResponseDto 리스트를 반환한다`() {
            // given
            val chatMessageList = ChatMessageUtil.createList()

            val roomId: Long = 1
            val cursor: String? = chatMessageList[0].id!!.toHexString()
            val size = 10

            val pageRequest = PageRequest.of(0, size)

            every{ chatMessageRepository.findLatestByRoomIdAndCursor(roomId, ObjectId(cursor), pageRequest) }.returns(chatMessageList)

            // when
            val result = service.getMessages(roomId, cursor, size)

            // then
            result.size shouldBe size
            for(i in 0 until size) {
                result[i].messageId shouldBe chatMessageList[i].id!!.toHexString()
            }
        }
    }

}