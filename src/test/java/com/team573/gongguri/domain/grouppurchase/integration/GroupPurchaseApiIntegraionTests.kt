package com.team573.gongguri.domain.grouppurchase.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.team573.gongguri.domain.grouppurchase.dto.GroupPurchaseRequestDto
import com.team573.gongguri.domain.member.entity.Member
import com.team573.gongguri.global.security.CustomUserDetails
import com.team573.gongguri.integration.AbstractIntegrationTest
import io.kotest.core.annotation.DisplayName

import jakarta.transaction.Transactional
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.*


class GroupPurchaseApiIntegrationTests : AbstractIntegrationTest() {

    @Test
    fun `공동구매 생성 성공`() {
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
        val mapper = ObjectMapper()
        mockMvc.post("/api/group-purchases") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(request)
            with(user(userDetails))
        }.andExpect {
            status { isCreated() }
            jsonPath("$.title") { value("공구리 공구") }
        }.andDo {
            print()
        }
    }

    @Test
    fun `공동구매 단일 조회 성공`() {
        val groupId = 10L
        mockMvc.get("/api/group-purchases/$groupId") {
            contentType = MediaType.APPLICATION_JSON
            with(user(userDetails))
        }.andExpect {
            status { isOk() }
            jsonPath("$.title") { value("페이징 [완료글] 1") }
        }.andDo {
            print()
        }
    }

    @Test
    fun `공동구매 삭제 성공`() {
        val groupId = 10L
        mockMvc.delete("/api/group-purchases/$groupId") {
            contentType = MediaType.APPLICATION_JSON
            with(user(userDetails))
        }.andExpect {
            status { isNoContent() }
        }.andDo {
            print()
        }
    }

    @Test
    fun `공동구매 수정 성공`() {
        val groupId = 10L
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
        val mapper = ObjectMapper()
        mockMvc.put("/api/group-purchases/$groupId") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(request)
            with(user(userDetails))
        }.andExpect {
            status { isOk() }
        }.andDo {
            print()
        }
    }

    @Test
    fun `공동구매 참여 성공`() {
        val groupId = 10L

        mockMvc.post("/api/group-purchases/$groupId/join") {
            contentType = MediaType.APPLICATION_JSON
            with(user(userDetails))
        }.andExpect {
            status { isOk() }
        }.andDo {
            print()
        }
    }
}