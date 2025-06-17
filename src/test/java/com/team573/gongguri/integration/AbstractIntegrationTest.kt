package com.team573.gongguri.integration

import com.team573.gongguri.domain.member.entity.Member
import com.team573.gongguri.domain.member.repository.MemberRepository
import com.team573.gongguri.global.security.CustomUserDetails
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc

abstract class AbstractIntegrationTest: ContainerSetUp() {
	@Autowired
	lateinit var mockMvc: MockMvc

	@Autowired
	lateinit var memberRepository: MemberRepository

	lateinit var member: Member

	lateinit var userDetails: CustomUserDetails

	@BeforeEach
	fun setUp() {
		member = memberRepository.findById(1).get()
		userDetails = CustomUserDetails(member)
	}
}