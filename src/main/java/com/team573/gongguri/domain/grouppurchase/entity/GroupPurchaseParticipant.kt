package com.team573.gongguri.domain.grouppurchase.entity

import com.team573.gongguri.domain.member.entity.Member
import com.team573.gongguri.global.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "group_purchase_participant")
class GroupPurchaseParticipant (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val groupParticipantId: Long? = null,

    var deposit:Boolean = false,

    @JoinColumn(name = "group_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    var groupPurchase: GroupPurchase,

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    var member: Member,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var participationStatus: ParticipationStatus
) : BaseEntity() {

    fun confirmDeposit() {
        this.deposit = true
    }

    fun cancelMember() {
        this.participationStatus = ParticipationStatus.CANCELLED
    }
}
