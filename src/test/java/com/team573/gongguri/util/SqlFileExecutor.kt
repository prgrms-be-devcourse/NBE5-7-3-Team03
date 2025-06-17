package com.team573.gongguri.util

import org.springframework.jdbc.core.JdbcTemplate
import java.nio.file.Files
import java.nio.file.Path
import java.nio.charset.StandardCharsets

object SqlFileExecutor {
    private lateinit var jdbcTemplate: JdbcTemplate

    private const val SQL_FILE_RELATIVE_PATH = "src/test/resources/dump-gongguri.sql"

    fun setJdbcTemplate(jdbcTemplate: JdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate
    }

    @Throws(Exception::class)
    fun executeSqlFile() {
        check(::jdbcTemplate.isInitialized) { "JdbcTemplate must be set before using this executor" }

        val path = Path.of(SQL_FILE_RELATIVE_PATH)
        val sql = Files.readString(path, StandardCharsets.UTF_8)

        val statements = sql.split(";")
        statements.forEach { stmt ->
            val trimmed = stmt.trim()
            if (trimmed.isNotEmpty()) {
                jdbcTemplate.execute(trimmed)
            }
        }
    }
}
