package com.team573.gongguri.domain.chat.mapper

import com.team573.gongguri.domain.chat.entity.ChatRoom
import com.team573.gongguri.domain.chat.entity.ChatRoomParticipation
import com.team573.gongguri.domain.member.entity.Member
import lombok.AccessLevel
import lombok.NoArgsConstructor

fun toParticipationEntity(member: Member, chatRoom: ChatRoom): ChatRoomParticipation {
    return ChatRoomParticipation(
        member = member,
        chatRoom = chatRoom
    )
}
