package com.team573.gongguri

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.mongodb.config.EnableMongoAuditing

@EnableJpaAuditing
@EnableMongoAuditing
@SpringBootApplication
class GongguriApplication

fun main(args: Array<String>) {
    runApplication<GongguriApplication>(*args)
}
