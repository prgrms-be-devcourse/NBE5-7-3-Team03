package com.team573.gongguri.integration

import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@ActiveProfiles("test")
abstract class AbstractIntegrationTest {
    companion object {
        @Container
        val mysqlContainer = MySQLContainer<Nothing>("mysql:8.0.33").apply {
            withDatabaseName("testdb")
            withUsername("testuser")
            withPassword("testpass")
            withExposedPorts(3306)
            start()
            System.setProperty("spring.datasource.url", jdbcUrl)
            System.setProperty("spring.datasource.username", username)
            System.setProperty("spring.datasource.password", password)
        }

        @Container
        val mongoContainer = MongoDBContainer("mongo:6.0.6").apply {
            start()
            val containerIpAddress = this.host
            val port = this.getMappedPort(27017)

            System.setProperty(
                "spring.data.mongodb.uri",
                "mongodb://$containerIpAddress:$port/testdb"
            )
        }

        @JvmStatic
        @DynamicPropertySource
        fun overrideProperties(registry: DynamicPropertyRegistry) {
            // MySQL 설정
            registry.add("spring.datasource.url") { mysqlContainer.jdbcUrl }
            registry.add("spring.datasource.username") { mysqlContainer.username }
            registry.add("spring.datasource.password") { mysqlContainer.password }

            // MongoDB 설정
            registry.add("spring.data.mongodb.uri") { mongoContainer.replicaSetUrl }
        }
    }
}