package com.team573.gongguri.domain.member.service;

import com.univcert.api.UnivCert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.io.IOException;

@Service
public class UnivCertificationService {
    @Value("${spring.certification.api-key}")
    private String API_KEY;

    // 인증 코드 발송 메서드
    public Map<String, Object> sendVerificationCode(String email, String universityName) throws IOException {
        return UnivCert.certify(API_KEY, email, universityName, false); // univ_check: 실존 대학 체크 (false: 개발용)
    }

    // 인증 코드 검증
    public boolean verifyEmailCode(String email, String universityName, String verificationCode) throws IOException {
        try {
            Map<String, Object> response = UnivCert.certifyCode(API_KEY, email, universityName, Integer.parseInt(verificationCode));
            return (boolean) response.get("success");  // 인증 성공 여부 확인
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
