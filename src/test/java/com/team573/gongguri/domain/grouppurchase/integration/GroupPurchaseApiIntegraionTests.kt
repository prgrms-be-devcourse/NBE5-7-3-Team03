package com.team573.gongguri.domain.grouppurchase.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.team573.gongguri.domain.grouppurchase.dto.GroupPurchaseRequestDto

import jakarta.transaction.Transactional
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class GroupPurchaseApiIntegrationTests {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper //json변환을 위한 jackson라이브러리 객체

    @Test
//    @DisplayName("공동구매 생성 성공")
    fun add() {
        val request = GroupPurchaseRequestDto(
            "공구리 공구",
            "쌉니다 싸요",
            10000,
            100,
            "여간기합은행",
            "1234567890",
            "RECRUITING",
            "image/jpeg"
        )
        val json = objectMapper.writeValueAsString(request);
        mockMvc.post("/api/group-purchases") {
            contentType = MediaType.APPLICATION_JSON
            content = json
        }.andExpect {
            status { isCreated() }
            jsonPath("$.title") {value("라면 나눠요")}
        }
    }
}