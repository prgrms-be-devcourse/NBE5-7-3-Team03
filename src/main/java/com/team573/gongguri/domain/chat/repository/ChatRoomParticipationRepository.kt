package com.team573.gongguri.domain.chat.repository

import com.team573.gongguri.domain.chat.entity.ChatRoom
import com.team573.gongguri.domain.chat.entity.ChatRoomParticipation
import com.team573.gongguri.domain.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatRoomParticipationRepository : JpaRepository<ChatRoomParticipation, Long> {
    fun existsByChatRoomAndMember(chatRoom: ChatRoom, member: Member): Boolean
    fun deleteByChatRoomAndMember(chatRoom: ChatRoom, member: Member)
}
