package com.team573.gongguri.domain.grouppurchase.service

import com.team573.gongguri.domain.grouppurchase.service.ImageUploadService
import com.team573.gongguri.global.exception.CustomErrorCode
import com.team573.gongguri.global.exception.CustomException
import io.kotest.matchers.shouldBe
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.mock.web.MockMultipartFile

@ExtendWith(MockKExtension::class)
class ImageUploadServiceTests {
    private val imageUploadService = ImageUploadService()

    @Test
    @DisplayName("imageUrl 반환 성공 테스트")
    fun uploadImageTests_sucessfully() {
        //given
        val mockFile = MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "image content".toByteArray()
        )
        //when
        val result = imageUploadService.uploadImage(mockFile)

        //then
        result["imageUrl"]!!.startsWith("/uploads/") shouldBe true
    }

    @Test
    @DisplayName("이미지 파일이 null일 경우 예외 테스트")
    fun uploadImageTests_imageFileEmpty_theowsException() {
        //given
        val mockFile: MockMultipartFile? = null

        //when & then
        val exception = assertThrows(CustomException::class.java) { imageUploadService.uploadImage(mockFile) }
        exception.getCustomErrorCode() shouldBe CustomErrorCode.INVALID_IMAGE_FILE
    }

    @Test
    @DisplayName("파일의 내용이 비어있는 경우 예외 테스트 ")
    fun testUploadImage_emptyFile_throwsException() {
        //given
        val mockFile = MockMultipartFile(
            "file",
            "empty.jpg",
            "image/jpeg",
            ByteArray(0) // 테스트 실패
        )

        //when & then
        val exception = assertThrows(CustomException::class.java) { imageUploadService.uploadImage(mockFile) }
        exception.getCustomErrorCode() shouldBe CustomErrorCode.INVALID_IMAGE_FILE
    }
}
