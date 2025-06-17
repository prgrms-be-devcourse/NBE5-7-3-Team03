package com.team573.gongguri.global

import com.team573.gongguri.integration.AbstractIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class FileExecutorIntegrationTests: AbstractIntegrationTest(){

    companion object {
        private val log: Logger = LoggerFactory.getLogger(FileExecutorIntegrationTests::class.java)
    }

    @Test
    fun `sql 파일 읽어서 MySQL 적재 테스트`() {
        val count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM member", Int::class.java)

        // 확인용 로그
        log.info("member 테이블 레코드 수: {}", count)

        assertThat(count).isGreaterThan(0)
    }

    @Test
    fun `json 파일 읽어서 몽고DB 적재 테스트`() {
        val collection = mongoTemplate.getCollection("chat_message")
        val count = collection.countDocuments()

        // 확인용 로그
        log.info("chat_message 컬렉션 문서 수: {}", count)
        collection.find().limit(5).forEach { doc ->
            log.info("문서: {}", doc.toJson())
        }

        assertThat(count).isGreaterThan(0)
    }
}