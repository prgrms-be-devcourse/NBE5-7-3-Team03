package com.team573.gongguri.util

import com.team573.gongguri.domain.chat.entity.ChatRoom

object ChatRoomUtil {
	fun createWithId(roomId: Long): ChatRoom {
		return ChatRoom(
			chatRoomId = roomId,
		)
	}
}