package com.team573.gongguri.domain.chat.entity;

import com.team573.gongguri.global.entity.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "chat_message")
@Getter
@NoArgsConstructor
public class ChatMessage extends BaseEntity {
    @Id
    private ObjectId id;

    private String nickname;

    private String content;

    @Field("room_id")
    private Long roomId;

    @Builder
    public ChatMessage(String content, Long roomId, String nickname) {
        this.content = content;
        this.roomId = roomId;
        this.nickname = nickname;
    }
}
