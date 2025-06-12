package com.team573.gongguri.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;

@Slf4j
@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(CustomException.class)
    public Object handleErrorException(
        CustomException e,
        Model model,
        HandlerMethod handlerMethod  // 현재 실행 중인 컨트롤러 메서드 정보
    ) {
        // @ResponseBody 또는 @RestController 가 있는지 확인
        boolean isApiRequest = AnnotatedElementUtils.hasAnnotation(handlerMethod.getBeanType(), ResponseBody.class);
        CustomErrorCode customErrorCode = e.getCustomErrorCode();

        // 에러 로깅
        log.error(customErrorCode.getMessage(), e);

        // Json 응답 (RestController)
        if (isApiRequest) {
            HttpStatus httpStatus = customErrorCode.getStatus();

            CustomErrorResponse response = CustomErrorResponse.builder()
                .code(customErrorCode.getCode())
                .message(customErrorCode.getMessage())
                .build();

            return ResponseEntity.status(httpStatus)
                .body(response);
        }

        // HTML 응답
        else {
            model.addAttribute("message", customErrorCode.getMessage());
            return "error/alert";
        }
    }
}
