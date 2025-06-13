import com.team573.gongguri.domain.chat.entity.ChatRoom
import com.team573.gongguri.domain.chat.entity.ChatRoomParticipation
import com.team573.gongguri.domain.member.entity.Member

object ChatParticipantFixture {
	fun createWithId(id: Long, member: Member, chatRoom: ChatRoom): ChatRoomParticipation {
		return ChatRoomParticipation(
			chatRoomParticipantId = id,
			member = member,
			chatRoom = chatRoom
		)
	}

	fun create(member: Member, chatRoom: ChatRoom): ChatRoomParticipation {
		return ChatRoomParticipation(
			member = member,
			chatRoom = chatRoom
		)
	}
}