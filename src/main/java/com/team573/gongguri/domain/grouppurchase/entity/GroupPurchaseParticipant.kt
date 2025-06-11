package com.team573.gongguri.domain.grouppurchase.entity;

import com.team573.gongguri.domain.member.entity.Member;
import com.team573.gongguri.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor
@Table(name = "group_purchase_participant")
public class GroupPurchaseParticipant extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupParticipantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private GroupPurchase groupPurchase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParticipationStatus participationStatus;

    private Boolean deposit = false;

    public void confirmDeposit() {
        this.deposit = true;
    }

    public void cancelMember() {
        this.participationStatus = ParticipationStatus.CANCELLED;
    }

    @Builder
    public GroupPurchaseParticipant(GroupPurchase groupPurchase, Member member, ParticipationStatus participationStatus) {
        this.groupPurchase = groupPurchase;
        this.member = member;
        this.participationStatus = participationStatus;
    }
}
