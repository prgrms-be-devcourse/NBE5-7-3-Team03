package com.team573.gongguri.util

import com.team573.gongguri.domain.member.entity.Member
import com.team573.gongguri.global.security.CustomUserDetails

object AuthUtil {
	fun createUserDetails(member: Member): CustomUserDetails {
		return CustomUserDetails(member)
	}
}
