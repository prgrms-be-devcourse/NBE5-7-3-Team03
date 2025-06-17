package com.team573.gongguri.domain.grouppurchase.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.team573.gongguri.domain.grouppurchase.dto.GroupPurchaseRequestDto
import com.team573.gongguri.domain.grouppurchase.entity.ProgressStatus
import com.team573.gongguri.domain.grouppurchase.repository.GroupPurchaseRepository
import com.team573.gongguri.global.security.CustomUserDetails
import com.team573.gongguri.integration.AbstractIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.CoreMatchers.containsString
import org.junit.jupiter.api.Nested

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.*


class GroupPurchaseApiIntegrationTests : AbstractIntegrationTest() {

    @Autowired
    lateinit var groupPurchaseRepository: GroupPurchaseRepository

    @Nested
    inner class CRUDtests {
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
        fun `공동구매 상세 조회 성공`() {
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
    }

   @Nested
   inner class JoinTests {
       @Test
       fun `공동구매 참여 성공`() {
           val groupId = 10L
           val otherMember = memberRepository.findById(9).get()
           userDetails = CustomUserDetails(otherMember)
           mockMvc.post("/api/group-purchases/$groupId/join") {
               contentType = MediaType.APPLICATION_JSON
               with(user(userDetails))
           }.andExpect {
               status { isNoContent() }
           }.andDo {
               print()
           }
       }

       @Test
       fun `참여 인원 초과 시 참여 실패`() {
           val groupId = 10L
           // 남은 자리 1개 채움
           val member1 = memberRepository.findById(17).get()
           val userDetails1 = CustomUserDetails(member1)
           mockMvc.post("/api/group-purchases/$groupId/join") {
               contentType = MediaType.APPLICATION_JSON
               with(user(userDetails1))
           }.andExpect {
               status { isNoContent() }
           }

           // 초과 시도
           val member2 = memberRepository.findById(9).get()
           val userDetails2 = CustomUserDetails(member2)
           mockMvc.post("/api/group-purchases/$groupId/join") {
               contentType = MediaType.APPLICATION_JSON
               with(user(userDetails2))
           }.andExpect {
               status { isBadRequest() }
           }.andDo {
               print()
           }
       }

       @Test
       fun `이미 참여 중인 경우`() {
           val groupId = 10L
           val member = memberRepository.findById(1).get()
           val userDetails = CustomUserDetails(member)

           mockMvc.post("/api/group-purchases/$groupId/join") {
               contentType = MediaType.APPLICATION_JSON
               with(user(userDetails))
           }.andExpect {
               status { isConflict() }
               content { string(containsString("이미 공동구매에 참여하였습니다.")) }
           }.andDo {
               print()
           }
       }

       @Test
       fun `참여 정원이 가득 찬 경우 Closed로 변경`() {
           val groupId = 10L
           val member = memberRepository.findById(9).get()
           val userDetails = CustomUserDetails(member)

           mockMvc.post("/api/group-purchases/$groupId/join") {
               contentType = MediaType.APPLICATION_JSON
               with(user(userDetails))
           }.andExpect {
               status { isNoContent() }
           }
           val closedGroupPurchase = groupPurchaseRepository.findById(groupId).get()

           assertThat(closedGroupPurchase.progressStatus).isEqualTo(ProgressStatus.CLOSED)
       }
   }
}