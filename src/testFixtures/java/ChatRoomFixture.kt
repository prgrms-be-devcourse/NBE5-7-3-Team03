import com.team573.gongguri.domain.chat.entity.ChatRoom

object ChatRoomFixture {
	fun createWithId(roomId: Long): ChatRoom {
		return ChatRoom(
			chatRoomId = roomId,
		)
	}
}