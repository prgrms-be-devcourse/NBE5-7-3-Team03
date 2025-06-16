package com.team573.gongguri.util

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.mongodb.client.MongoCollection
import org.bson.Document
import java.nio.file.Files
import java.nio.file.Path
import java.nio.charset.StandardCharsets

object MongoJsonExecutor {

	private lateinit var collection: MongoCollection<Document>

	private const val JSON_FILE_RELATIVE_PATH = "src/test/resources/chat_message.json"

	fun setCollection(collection: MongoCollection<Document>) {
		this.collection = collection
	}

	@Throws(Exception::class)
	fun executeJsonFile() {
		check(::collection.isInitialized) { "MongoCollection must be set before using this executor" }

		val path = Path.of(JSON_FILE_RELATIVE_PATH)
		val json = Files.readString(path, StandardCharsets.UTF_8)

		// jacksonObjectMapper를 이용해 JSON 배열 파싱 (List<Document>로 변환)
		val mapper = jacksonObjectMapper()
		val documents: List<Map<String, Any>> = mapper.readValue(json)

		// Map을 Document로 변환하여 insertMany 실행
		val bsonDocuments = documents.map { Document(it) }
		collection.insertMany(bsonDocuments)
	}
}