package com.team573.gongguri.util

import com.team573.gongguri.domain.chat.entity.ChatMessage
import org.bson.types.ObjectId

object ChatMessageUtil {
	fun createWithId(): ChatMessage {
		return ChatMessage(
			id = ObjectId(),
			content = "test",
			roomId = 1,
			nickname = "test1"
		)
	}

	fun createList(): List<ChatMessage> {
		val list: MutableList<ChatMessage> = mutableListOf()

		for(i in 1..10) {
			list.add(createWithId())
		}

		return list
	}
}