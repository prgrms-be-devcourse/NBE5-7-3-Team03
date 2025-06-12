package com.team573.gongguri.global.exception

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.http.ResponseEntity
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.method.HandlerMethod

@ControllerAdvice
class ExceptionAdvice {

    val log: Logger = LoggerFactory.getLogger(ExceptionAdvice::class.java)

    @ExceptionHandler(CustomException::class)
    fun handleErrorException(
        e: CustomException,
        model: Model,
        handlerMethod: HandlerMethod // 현재 실행 중인 컨트롤러 메서드 정보
    ): Any {
        // @ResponseBody 또는 @RestController 가 있는지 확인
        val isApiRequest = AnnotatedElementUtils.hasAnnotation(handlerMethod.beanType, ResponseBody::class.java)
        val customErrorCode = e.getCustomErrorCode()

        // 에러 로깅
        log.error(customErrorCode.getMessage(), e)

        // Json 응답 (RestController)
        if (isApiRequest) {
            val httpStatus = customErrorCode.getHttpStatus()

            val response = CustomErrorResponse(
                code =  customErrorCode.getCode(),
                message =  customErrorCode.getMessage(),
            )

            return ResponseEntity.status(httpStatus)
                .body(response)
        } else {
            model.addAttribute("message", customErrorCode.getMessage())
            return "error/alert"
        }
    }
}
