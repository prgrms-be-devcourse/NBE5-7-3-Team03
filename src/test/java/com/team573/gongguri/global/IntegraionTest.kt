package com.team573.gongguri.global

import com.team573.gongguri.integration.AbstractIntegrationTest
import com.team573.gongguri.util.MongoJsonExecutor
import com.team573.gongguri.util.SqlFileExecutor
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@TestPropertySource(properties = ["UNIV_CERTIFICATION_API_KEY=test-key"])
@ActiveProfiles("test")
class FileExecutorIntegrationTests(): AbstractIntegrationTest(){
	@Autowired
	lateinit var jdbcTemplate: JdbcTemplate
	@Autowired
	lateinit var mongoTemplate: MongoTemplate

	companion object {
		private val log: Logger = LoggerFactory.getLogger(FileExecutorIntegrationTests::class.java)
	}
	@Test
	fun `sql 파일 읽어서 MySQL 적재 테스트`() {
		SqlFileExecutor.setJdbcTemplate(jdbcTemplate)
		SqlFileExecutor.executeSqlFile()

		val count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM member", Int::class.java)

		// 확인용 로그
		log.info("member 테이블 레코드 수: {}", count)

		assertThat(count).isGreaterThan(0)
	}
	@Test
	fun `json 파일 읽어서 몽고DB 적재 테스트`() {
		val collection = mongoTemplate.getCollection("chat_message")
		MongoJsonExecutor.setCollection(collection)
		MongoJsonExecutor.executeJsonFile()
		val count = collection.countDocuments()

		// 확인용 로그
		log.info("chat_message 컬렉션 문서 수: {}", count)
		collection.find().limit(5).forEach { doc ->
			log.info("문서: {}", doc.toJson())
		}

		assertThat(count).isGreaterThan(0)
	}
}
