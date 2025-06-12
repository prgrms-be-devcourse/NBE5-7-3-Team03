package com.team573.gongguri.global.exception


class CustomException(private val customErrorCode: CustomErrorCode) : RuntimeException() {
    fun getCustomErrorCode(): CustomErrorCode = customErrorCode
}
