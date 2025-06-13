package com.team573.gongguri.domain.member.repository

import com.team573.gongguri.domain.member.entity.Univ
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UnivRepository : JpaRepository<Univ, Long?> {
    fun findByUnivName(univName: String): Univ?
}
