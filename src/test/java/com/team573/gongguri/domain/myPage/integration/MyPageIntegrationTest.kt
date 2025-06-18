package com.team573.gongguri.domain.myPage.integration

import com.team573.gongguri.global.security.CustomUserDetails
import com.team573.gongguri.integration.AbstractIntegrationTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.get

class MyPageIntegrationTest: AbstractIntegrationTest(){

    @Nested
    @DisplayName("마이페이지 통합 테스트")
    inner class JoinTest {

        @Test
        fun `멤버가 참여한 공구글을 반환한다`() {
            // when
            mockMvc.get("/my-page/purchase") {
                contentType = MediaType.APPLICATION_JSON
                with(user(userDetails))
            }
                //then
                .andExpect {
                    status { isOk() }
                    view { name("myPage/purchase") }
                    model {
                        attributeExists("participatedList")
                    }
                }
        }

        @Test
        fun `멤버의 정보와 작성한 글을 반환한다`() {
            // when
            mockMvc.get("/my-page/profile") {
                contentType = MediaType.APPLICATION_JSON
                with(user(userDetails))
            }
                //then
                .andExpect {
                    status { isOk() }
                    view { name("myPage/profile") }
                    model {
                        attributeExists("nickname")
                        attributeExists("likeCount")
                        attributeExists("dislikeCount")
                        attributeExists("status")
                        attributeExists("createdList")
                    }
                }
        }
    }
}