package com.team573.gongguri.domain.chat.entity

import com.team573.gongguri.global.entity.BaseEntity
import jakarta.persistence.EntityListeners
import lombok.Builder
import lombok.Getter
import lombok.NoArgsConstructor
import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.time.LocalDateTime


@Document(collection = "chat_message")
class ChatMessage (
    @Id
    val id: ObjectId? = null,

    var content: String,

    @Field("room_id")
    var roomId: Long,

    var nickname: String,
) {
    @CreatedDate
    var createdAt: LocalDateTime? = null

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
}
