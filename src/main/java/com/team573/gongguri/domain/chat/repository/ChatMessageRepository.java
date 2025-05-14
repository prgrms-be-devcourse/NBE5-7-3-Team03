package com.team573.gongguri.domain.chat.repository;

import com.team573.gongguri.domain.chat.entity.ChatMessage;
import com.team573.gongguri.global.annotation.ExcludeFromJpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
@ExcludeFromJpaRepository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

}
