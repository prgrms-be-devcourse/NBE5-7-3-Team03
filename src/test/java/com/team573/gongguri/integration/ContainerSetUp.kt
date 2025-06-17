package com.team573.gongguri.integration

import com.team573.gongguri.util.MongoJsonExecutor
import com.team573.gongguri.util.SqlFileExecutor
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = ["UNIV_CERTIFICATION_API_KEY=test-key"])
@ActiveProfiles("test")
@Transactional
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class ContainerSetUp {
    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    companion object {

        val mysqlContainer = MySQLContainer("mysql:8.0").apply {
            withDatabaseName("testdb")
            withUsername("testuser")
            withPassword("testpass")
            withReuse(true)
            start()
        }

        val mongoContainer = MongoDBContainer("mongo:6.0.6").apply{
            withReuse(true)
            start()
        }

        @JvmStatic
        @DynamicPropertySource
        fun overrideProps(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl)
            registry.add("spring.datasource.username", mysqlContainer::getUsername)
            registry.add("spring.datasource.password", mysqlContainer::getPassword)
            registry.add("spring.data.mongodb.uri", mongoContainer::getReplicaSetUrl)
        }

    }

    @BeforeAll
    fun dataSetUp() {
        SqlFileExecutor.setJdbcTemplate(jdbcTemplate)
        SqlFileExecutor.executeSqlFile()

        val collection = mongoTemplate.getCollection("chat_message")
        MongoJsonExecutor.setCollection(collection)
        MongoJsonExecutor.executeJsonFile()
    }

    @AfterAll
    fun clearData() {
        mongoTemplate.dropCollection("chat_message")
    }
}