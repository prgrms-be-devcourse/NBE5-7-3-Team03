package com.team573.gongguri.domain.member.service

import com.univcert.api.UnivCert
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class UnivCertificationService {
    @Value("\${spring.certification.api-key}")
    private lateinit var apiKey: String

    // 인증 코드 발송 메서드
    @Throws(IOException::class)
    fun sendVerificationCode(email: String, universityName: String): Map<String, Any> {
        return UnivCert.certify(apiKey, email, universityName, false)
    }

    // 인증 코드 검증
    @Throws(IOException::class)
    fun verifyEmailCode(email: String, universityName: String, verificationCode: String): Boolean {
        return try {
            val response = UnivCert.certifyCode(apiKey, email, universityName, verificationCode.toInt())
            // success 값이 Boolean이 아니면 false 반환
            response["success"] as? Boolean ?: false
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }
}
