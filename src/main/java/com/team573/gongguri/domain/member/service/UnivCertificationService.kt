package com.team573.gongguri.domain.member.service

import com.team573.gongguri.global.exception.CustomErrorCode
import com.team573.gongguri.global.exception.CustomException
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
            val success = response["success"] as? Boolean ?: false
            if (!success) {
                throw CustomException(CustomErrorCode.VERIFICATION_CODE_MISMATCH)
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            throw CustomException(CustomErrorCode.VERIFICATION_SERVER_ERROR)
        }
    }
}
