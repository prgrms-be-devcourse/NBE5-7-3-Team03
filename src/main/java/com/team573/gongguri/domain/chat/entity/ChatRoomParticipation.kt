package com.team573.gongguri.domain.chat.entity

import com.team573.gongguri.domain.member.entity.Member
import com.team573.gongguri.global.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "chat_room_participation")
class ChatRoomParticipation (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val chatRoomParticipantId: Long? = null,

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private var member: Member,

    @JoinColumn(name = "chat_room_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private var chatRoom: ChatRoom
) : BaseEntity() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChatRoomParticipation

        if (chatRoomParticipantId != other.chatRoomParticipantId) return false
        if (createdAt != other.createdAt) return false


        return true
    }

    override fun hashCode(): Int {
        var result = chatRoomParticipantId?.hashCode() ?: 0
        result = 31 * result + createdAt.hashCode()
        return result
    }
}


