package com.team573.gongguri.domain.chat.repository;

import com.team573.gongguri.domain.chat.entity.ChatMessage;
import com.team573.gongguri.global.annotation.ExcludeFromJpaRepository;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
@ExcludeFromJpaRepository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    @Query(value = "{ 'roomId': ?0 }", sort = "{ '_id': -1 }")
    List<ChatMessage> findLatestByRoomId(Long roomId, Pageable pageable);

    @Query(value = "{ 'roomId': ?0, '_id': { '$lt': ?1 } }", sort = "{ '_id': -1 }")
    List<ChatMessage> findLatestByRoomIdAndCursor(Long roomId, ObjectId cursor, Pageable pageable);

}
