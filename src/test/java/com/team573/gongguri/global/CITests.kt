package com.team573.gongguri.global

import com.team573.gongguri.integration.AbstractIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.test.annotation.DirtiesContext

//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class CITests : AbstractIntegrationTest(){
    @Test
    fun `컨테이너 정상 기동 확인`() {
        println("MySQL running at: ${mysqlContainer.jdbcUrl}")
        println("Mongo running at: ${mongoContainer.replicaSetUrl}")
        assert(mysqlContainer.isRunning)
        assert(mongoContainer.isRunning)
    }
}