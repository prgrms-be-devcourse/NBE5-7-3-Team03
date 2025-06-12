package com.team573.gongguri.domain.chat.repository

import com.team573.gongguri.domain.chat.entity.ChatMessage
import com.team573.gongguri.global.annotation.ExcludeFromJpaRepository
import org.bson.types.ObjectId
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

@Repository
@ExcludeFromJpaRepository
interface ChatMessageRepository : MongoRepository<ChatMessage, String> {
    @Query(value = "{ 'roomId': ?0 }", sort = "{ '_id': -1 }")
    fun findLatestByRoomId(roomId: Long, pageable: Pageable): List<ChatMessage>

    @Query(value = "{ 'roomId': ?0, '_id': { '\$lt': ?1 } }", sort = "{ '_id': -1 }")
    fun findLatestByRoomIdAndCursor(roomId: Long, cursor: ObjectId, pageable: Pageable): List<ChatMessage>
}
