package com.team573.gongguri.domain.grouppurchase.entity

import com.team573.gongguri.domain.chat.entity.ChatRoom
import com.team573.gongguri.domain.member.entity.Member
import com.team573.gongguri.domain.member.entity.Univ
import com.team573.gongguri.global.entity.BaseEntity
import jakarta.persistence.*
import lombok.Setter

@Entity
@Table(name = "group_purchase")
class GroupPurchase (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val groupId: Long? = null,

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    var member: Member,

    @JoinColumn(name = "univ_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    var univ: Univ,

    @JoinColumn(name = "chat_room_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    var chatRoom: ChatRoom,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Setter
    var progressStatus: ProgressStatus,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false)
    var content: String,

    @Column(nullable = false)
    var price: Int,

    @Column(nullable = false)
    var maxParticipants: Int,

    @Column(nullable = false)
    var bank: String,

    @Column(nullable = false)
    var account: String,

    @Column(nullable = true)
    @Setter
    var imageUrl: String,

    @Column(name = "is_deleted", nullable = false)
    var deleted:Boolean = false

) : BaseEntity() {

    fun update(
        title: String,
        content: String,
        price: Int,
        maxParticipants: Int,
        bank: String,
        account: String,
        progressStatus: ProgressStatus

    ) {
        this.title = title
        this.content = content
        this.price = price
        this.maxParticipants = maxParticipants
        this.bank = bank
        this.account = account
        this.progressStatus = progressStatus
    }

    fun markAsDeleted() {
        this.deleted = true
    }
}
