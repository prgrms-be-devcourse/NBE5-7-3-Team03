package com.team573.gongguri.domain.review.mapper;

import com.team573.gongguri.domain.grouppurchase.entity.GroupPurchase;
import com.team573.gongguri.domain.member.entity.Member;
import com.team573.gongguri.domain.review.entity.Review;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReviewMapper {

    public static Review toEntity(GroupPurchase groupPurchase, Member member, Boolean like) {
        return Review.builder()
            .groupPurchase(groupPurchase)
            .member(member)
            .like(like)
            .build();
    }
}
