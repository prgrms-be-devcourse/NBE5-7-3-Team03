package com.team573.gongguri.domain.grouppurchase.entity;

import com.team573.gongguri.domain.chat.entity.ChatRoom;
import com.team573.gongguri.domain.member.entity.Member;
import com.team573.gongguri.domain.member.entity.Univ;
import com.team573.gongguri.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@NoArgsConstructor
@Table(name = "group_purchase")
    public class GroupPurchase extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "univ_id", nullable = false)
    private Univ univ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProgressStatus progressStatus;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int maxParticipants;

    @Column(nullable = false)
    private String bank;

    @Column(nullable = false)
    private String account;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;

    @Setter
    @Column(nullable = true)
    private String imageUrl;

    @Builder
    public GroupPurchase (
            Member member,
            Univ univ,
            ChatRoom chatRoom,
            ProgressStatus progressStatus,
            String title,
            String content,
            int price,
            int maxParticipants,
            String bank,
            String account,
            String imageUrl
    ) {
        this.member = member;
        this.univ = univ;
        this.chatRoom = chatRoom;
        this.progressStatus = progressStatus;
        this.title = title;
        this.content = content;
        this.price = price;
        this.maxParticipants = maxParticipants;
        this.bank = bank;
        this.account = account;
        this.imageUrl = imageUrl;
    }

    public void update(
            String title,
            String content,
            int price,
            int maxParticipants,
            String bank,
            String account,
            ProgressStatus progressStatus

    ) {
        this.title = title;
        this.content = content;
        this.price = price;
        this.maxParticipants = maxParticipants;
        this.bank = bank;
        this.account = account;
        this.progressStatus = progressStatus;
    }

    public void markAsDeleted() {
        this.deleted = true;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setProgressStatus(ProgressStatus progressStatus) {
        this.progressStatus = progressStatus;
    }
}
