package com.team573.gongguri.global.exception;

import lombok.Builder;

@Builder
public record CustomErrorResponse(String code, String message) {

}
