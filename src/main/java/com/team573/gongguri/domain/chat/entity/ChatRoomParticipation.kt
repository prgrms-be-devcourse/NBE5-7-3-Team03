package com.team573.gongguri.domain.chat.entity

import com.team573.gongguri.domain.member.entity.Member
import com.team573.gongguri.global.entity.BaseEntity
import jakarta.persistence.*
import lombok.Builder
import lombok.Getter
import lombok.NoArgsConstructor

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
) : BaseEntity()


