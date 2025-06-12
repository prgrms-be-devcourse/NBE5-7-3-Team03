package com.team573.gongguri.domain.chat.repository

import com.team573.gongguri.domain.chat.entity.ChatRoom
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ChatRoomRepository : JpaRepository<ChatRoom, Long> {
    @Query("SELECT gp.chatRoom FROM GroupPurchase gp WHERE gp.groupId = :groupId")
    fun findChatRoomByGroupId(@Param("groupId") groupId: Long): ChatRoom
}
