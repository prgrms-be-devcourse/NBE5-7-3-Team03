package com.team573.gongguri.domain.chat.repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomChatMessageRepository {
    private final MongoTemplate mongoTemplate;

    public Map<Long, String> findLatestMessageByRoomIds(List<Long> chatRoomIds) {
        if (chatRoomIds == null || chatRoomIds.isEmpty()) {
            return Collections.emptyMap();
        }

        MatchOperation matchStage = Aggregation.match(Criteria.where("room_id").in(chatRoomIds));
        SortOperation sortStage = Aggregation.sort(Sort.by(Sort.Direction.DESC, "createdAt"));
        GroupOperation groupStage = Aggregation.group("room_id")
            .first("content").as("latestContent");

        Aggregation aggregation = Aggregation.newAggregation(
            matchStage,
            sortStage,
            groupStage
        );

        // Document로 결과 받기
        AggregationResults<Document> results = mongoTemplate.aggregate(
            aggregation,
            "chat_message",
            Document.class
        );

        Map<Long, String> latestMessageContents = new HashMap<>();

        for (Document doc : results) {
            Long roomId = doc.get("_id", Long.class);
            String content = doc.getString("latestContent");
            if (content != null) {
                latestMessageContents.put(roomId, content);
            }
        }

        return latestMessageContents;
    }
}
