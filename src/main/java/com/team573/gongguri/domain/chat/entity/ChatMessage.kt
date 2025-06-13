package com.team573.gongguri.domain.chat.entity

import com.team573.gongguri.global.entity.BaseEntity
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field


@Document(collection = "chat_message")
class ChatMessage (
    @Id
    val id: ObjectId? = null,

    var content: String,

    @Field("room_id")
    var roomId: Long,

    var nickname: String,
): BaseEntity() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChatMessage

        if (roomId != other.roomId) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = roomId.hashCode()
        result = 31 * result + (id?.hashCode() ?: 0)

        return result
    }
}
