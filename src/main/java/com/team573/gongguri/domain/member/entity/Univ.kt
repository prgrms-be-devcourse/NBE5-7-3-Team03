package com.team573.gongguri.domain.member.entity

import com.team573.gongguri.global.entity.BaseEntity
import jakarta.persistence.*
import lombok.Getter
import lombok.NoArgsConstructor

@Entity
@Table(name = "univ")
class Univ(

    @Column(nullable = false)
    val univName: String

) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val univId: Long? = null

}