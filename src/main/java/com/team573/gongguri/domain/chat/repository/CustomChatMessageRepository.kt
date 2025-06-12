package com.team573.gongguri.domain.chat.repository

import org.bson.Document
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.AggregationResults
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.stereotype.Repository

@Repository
class CustomChatMessageRepository(
    private val mongoTemplate: MongoTemplate
) {

    fun findLatestMessageByRoomIds(chatRoomIds: List<Long>): Map<Long, String> {
        if (chatRoomIds.isEmpty()) {
            return emptyMap()
        }

        // 최근 메세지 조회 조건 설정
        val aggregation = this.createLatestMessageAggregation(chatRoomIds)

        // Document로 결과 받기
        val results = mongoTemplate.aggregate(
            aggregation,
            "chat_message",
            Document::class.java
        )

        return this.resultsToMap(results)
    }

    // 최근 메세지 조회 조건 설정
    private fun createLatestMessageAggregation(chatRoomIds: List<Long>): Aggregation {
        return Aggregation.newAggregation(
            Aggregation.match(Criteria.where("room_id").`in`(chatRoomIds)),
            Aggregation.sort(Sort.by(Sort.Direction.DESC, "createdAt")),
            Aggregation.group("room_id")
                .first("content").`as`("latestContent")
        )
    }

    // Map으로 변환
    private fun resultsToMap(results: AggregationResults<Document>): Map<Long, String> {
        val latestMessageContents: MutableMap<Long, String> = HashMap()

        for (doc in results) {
            val roomId = doc["_id"] as Long
            val content = doc.getString("latestContent")
            if (content != null) {
                latestMessageContents[roomId] = content
            }
        }

        return latestMessageContents
    }
}
