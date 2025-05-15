package com.team573.gongguri.domain.myPage.controller;

import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseResponseDto;
import com.team573.gongguri.domain.myPage.service.MyPageService;
import com.team573.gongguri.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/my-page")
public class MyPageRestController {
    private final MyPageService myPageService;

    // 내가 작성한 공구글 조회
    @GetMapping("/created")
    public ResponseEntity<List<GroupPurchaseResponseDto>> getMyCreatedPurchases(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "ALL") String status) {
        Long memberId = userDetails.getMemberId();
        return ResponseEntity.ok(myPageService.findMyCreatedPurchases(memberId,status));
    }

    // 내가 참여한 공구글 조회
    @GetMapping("/participated")
    public ResponseEntity<List<GroupPurchaseResponseDto>> getMyParticipatedPurchases(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = userDetails.getMemberId();
        return ResponseEntity.ok(myPageService.findMyParticipatedPurchases(memberId));
    }


}
