package com.team573.gongguri

import com.team573.gongguri.integration.AbstractIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@TestPropertySource(properties = ["UNIV_CERTIFICATION_API_KEY=test-key"])
@ActiveProfiles("test")
class GongguriApplicationTests : AbstractIntegrationTest() {
	@Test
	fun contextLoads() {
	}
}
