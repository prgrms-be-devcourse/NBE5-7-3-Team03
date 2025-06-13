package com.team573.gongguri.domain.member.controller;

import com.team573.gongguri.domain.member.service.UnivCertificationService;
import com.univcert.api.UnivCert;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class UnivCertificationController {
    @Value("${spring.certification.api-key}")
    private String API_KEY;
    private final UnivCertificationService univCertificationService;


    // 이메일 인증 코드 발송 요청 처리
    @GetMapping("/send-verification-code")
    @ResponseBody
    public Map<String, Object> sendVerificationCode(@RequestParam String email, @RequestParam String univName) {
        try {
            Map<String, Object> response = univCertificationService.sendVerificationCode(email, univName);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return Map.of("success", false, "message", "인증 코드 발송에 실패했습니다.");
        }
    }

    @GetMapping("/verify-email-code")
    @ResponseBody
    public Map<String, Object> verifyEmailCode(
            @RequestParam String email,
            @RequestParam String univName,
            @RequestParam String verificationCode) {

        Map<String, Object> response = new HashMap<>();
        try {
            boolean isVerified = univCertificationService.verifyEmailCode(email, univName, verificationCode);
            response.put("success", isVerified);
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "인증 확인 중 오류 발생");
        }
        return response;
    }

    @GetMapping("/clear-cert")
    @ResponseBody
    public ResponseEntity<String> clearCert() {
        try {
            UnivCert.clear(API_KEY);
            return ResponseEntity.ok("인증 정보 초기화 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("초기화 실패: " + e.getMessage());
        }
    }
}