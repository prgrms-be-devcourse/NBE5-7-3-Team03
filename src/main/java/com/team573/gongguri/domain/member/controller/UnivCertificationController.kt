package com.team573.gongguri.domain.member.controller

import com.team573.gongguri.domain.member.service.UnivCertificationService
import com.team573.gongguri.global.dto.GeneralApiResponse
import com.team573.gongguri.global.exception.CustomErrorCode
import com.univcert.api.UnivCert
import lombok.RequiredArgsConstructor
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.io.IOException

@Controller
@RequiredArgsConstructor
class UnivCertificationController(
    private val univCertificationService: UnivCertificationService
) {
    @Value("\${spring.certification.api-key}")
    private lateinit var apiKey: String

    // 이메일 인증 코드 발송 요청 처리
    @GetMapping("/send-verification-code")
    @ResponseBody
    fun sendVerificationCode(
        @RequestParam email: String,
        @RequestParam univName: String
    ): GeneralApiResponse<Map<String, Boolean>> {
        return try {
            univCertificationService.sendVerificationCode(email, univName)

            GeneralApiResponse(
                data = mapOf("success" to true),
                msg = "인증 코드가 성공적으로 발송되었습니다.",
                code = "SUCCESS",
                status = HttpStatus.OK.value()
            )
        } catch (e: IOException) {
            e.printStackTrace()
            val msg = CustomErrorCode.VERIFICATION_SEND_FAILED.getMessage()
            val code = CustomErrorCode.VERIFICATION_SEND_FAILED.getCode()
            val status = CustomErrorCode.VERIFICATION_SEND_FAILED.getHttpStatus().value()

            GeneralApiResponse(
                data = mapOf("success" to false),
                msg = msg,
                code = code,
                status = status
            )
        }
    }

    @GetMapping("/verify-email-code")
    @ResponseBody
    fun verifyEmailCode(
        @RequestParam email: String,
        @RequestParam univName: String,
        @RequestParam verificationCode: String
    ): GeneralApiResponse<Map<String, Boolean>> {
        return try {
            val isVerified = univCertificationService.verifyEmailCode(email, univName, verificationCode)
            GeneralApiResponse(
                data = mapOf("success" to isVerified),
                msg = if (isVerified) "이메일 인증에 성공했습니다." else CustomErrorCode.VERIFICATION_CODE_MISMATCH.getMessage(),
                code = if (isVerified) "SUCCESS" else CustomErrorCode.VERIFICATION_CODE_MISMATCH.getCode(),
                status = if (isVerified) HttpStatus.OK.value()
                else CustomErrorCode.VERIFICATION_CODE_MISMATCH.getHttpStatus().value()
            )
        } catch (e: IOException) {
            e.printStackTrace()
            GeneralApiResponse(
                data = null,
                msg = CustomErrorCode.VERIFICATION_CHECK_FAILED.getMessage(),
                code = CustomErrorCode.VERIFICATION_CHECK_FAILED.getCode(),
                status = CustomErrorCode.VERIFICATION_CHECK_FAILED.getHttpStatus().value()
            )
        }
    }

    @GetMapping("/clear-cert")
    @ResponseBody
    fun clearCert(): GeneralApiResponse<Nothing> {
        return try {
            UnivCert.clear(apiKey)
            GeneralApiResponse(
                data = null,
                msg = "인증 정보 초기화 완료",
                code = "SUCCESS",
                status = HttpStatus.OK.value()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            GeneralApiResponse(
                data = null,
                msg = CustomErrorCode.CERT_CLEAR_FAILED.getMessage(),
                code = CustomErrorCode.CERT_CLEAR_FAILED.getCode(),
                status = CustomErrorCode.CERT_CLEAR_FAILED.getHttpStatus().value()
            )
        }
    }
}