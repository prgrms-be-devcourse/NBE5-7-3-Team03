package com.team573.gongguri.global.exception

import org.springframework.http.HttpStatus

enum class ErrorStatus(
    private val httpStatus: HttpStatus
) {
    BAD_REQUEST(HttpStatus.BAD_REQUEST),
    FORBIDDEN(HttpStatus.FORBIDDEN),
    NOT_FOUND(HttpStatus.NOT_FOUND),
    CONFLICT(HttpStatus.CONFLICT),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED);

    fun getHttpStatus(): HttpStatus = httpStatus
}
