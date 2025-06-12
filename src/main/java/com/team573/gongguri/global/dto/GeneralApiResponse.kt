package com.team573.gongguri.global.dto

data class GeneralApiResponse<T>(
    val data: T? = null,
    val msg: String,
    val code: String,
    val status: Int, // HTTP 상태 코드
)