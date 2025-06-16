package com.team573.gongguri.global

import com.team573.gongguri.GongguriApplicationTests.Companion.mongoContainer
import com.team573.gongguri.GongguriApplicationTests.Companion.mysqlContainer
import org.junit.jupiter.api.Test

class CITests {
    @Test
    fun `컨테이너 정상 기동 확인`() {
        println("MySQL running at: ${mysqlContainer.jdbcUrl}")
        println("Mongo running at: ${mongoContainer.replicaSetUrl}")
        assert(mysqlContainer.isRunning)
        assert(mongoContainer.isRunning)
    }
}