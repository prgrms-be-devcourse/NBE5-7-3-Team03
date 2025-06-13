package com.team573.gongguri.domain.chat.entity

import com.team573.gongguri.global.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "chat_room")
class ChatRoom(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val chatRoomId: Long? = null
) : BaseEntity() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChatRoom

        if (chatRoomId != other.chatRoomId) return false

        return true
    }

    override fun hashCode(): Int {
        return chatRoomId?.hashCode() ?: 0
    }
}
