package com.team573.gongguri.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus {
    BAD_REQUEST(HttpStatus.BAD_REQUEST),
    FORBIDDEN(HttpStatus.FORBIDDEN),
    NOT_FOUND(HttpStatus.NOT_FOUND),
    CONFLICT(HttpStatus.CONFLICT),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED);

    private final HttpStatus httpStatus;
}
