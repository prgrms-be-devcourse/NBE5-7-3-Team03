package com.team573.gongguri.domain.member.entity

import com.team573.gongguri.global.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "member")
class Member(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val memberId: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "univ_id")
    val univ: Univ,

    @Column(nullable = false)
    val email: String,

    @Column(unique = true)
    val nickname: String,

    @Column(nullable = false)
    val password: String,

    @Column(nullable = false)
    var likeCount: Int = 0,

    @Column(nullable = false)
    var dislikeCount: Int = 0

) : BaseEntity() {

    fun updateLikeCount(like: Boolean) {
        if (like) {
            likeCount++
        } else {
            dislikeCount++
        }
    }
}
