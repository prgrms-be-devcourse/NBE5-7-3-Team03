package com.team573.gongguri.domain.member.repository

import com.team573.gongguri.domain.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberRepository : JpaRepository<Member, Long> {
    fun findByMemberId(id: Long): Member?

//    fun findByEmail(email: String?): Optional<Member> // TODO 참조 Service(Chat,UserDetails) 마이그레이션 이후 `Member?`로 반환 타입 변경 필요

    fun findByEmailOrNull(email: String?): Member?

    fun existsByEmail(email: String): Boolean

    fun existsByNickname(nickname: String): Boolean
}
