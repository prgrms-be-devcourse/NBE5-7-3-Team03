package com.team573.gongguri.domain.chat.mapper;

import com.team573.gongguri.domain.chat.entity.ChatRoom;
import com.team573.gongguri.domain.chat.entity.ChatRoomParticipation;
import com.team573.gongguri.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoomMapper {
    public static ChatRoomParticipation toParticipationEntity(Member member, ChatRoom chatRoom) {
        return ChatRoomParticipation.builder()
            .member(member)
            .chatRoom(chatRoom)
            .build();
    }
}
