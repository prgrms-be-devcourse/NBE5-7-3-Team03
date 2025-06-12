package com.team573.gongguri.global.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final CustomErrorCode customErrorCode;

    public CustomException(CustomErrorCode customErrorCode) {
        this.customErrorCode = customErrorCode;
    }
}
