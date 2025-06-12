package com.team573.gongguri.domain.chat.repository;

import com.team573.gongguri.domain.chat.entity.ChatRoom;
import com.team573.gongguri.domain.chat.entity.ChatRoomParticipation;
import com.team573.gongguri.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomParticipationRepository extends JpaRepository<ChatRoomParticipation, Long> {
    Boolean existsByChatRoomAndMember(ChatRoom chatRoom, Member member);
    void deleteByChatRoomAndMember(ChatRoom chatRoom, Member member);
}
