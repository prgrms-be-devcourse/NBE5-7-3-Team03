package com.team573.gongguri

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container

@SpringBootTest
@TestPropertySource(properties = ["UNIV_CERTIFICATION_API_KEY=test-key"])
@ActiveProfiles("test")
class GongguriApplicationTests {

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
	}
	@Test
	fun contextLoads() {
	}
}
