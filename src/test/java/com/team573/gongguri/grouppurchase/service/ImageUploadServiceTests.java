package com.team573.gongguri.grouppurchase.service;

import com.team573.gongguri.domain.grouppurchase.service.ImageUploadService;
import com.team573.gongguri.global.exception.CustomErrorCode;
import com.team573.gongguri.global.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
public class ImageUploadServiceTests {

    private ImageUploadService imageUploadService = new ImageUploadService();

    @Test
    @DisplayName("imageUrl 반환 성공 테스트")
    void uploadImageTests_sucessfully() {
        //given
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "image content".getBytes()
        );
        //when
        Map<String, String> result = imageUploadService.uploadImage(mockFile);

        //then
        System.out.println("RESULT: " + result);
        System.out.println("imageUrl: " + result.get("imageUrl"));
        assertNotNull(result.get("imageUrl"));
        assertTrue(result.get("imageUrl").startsWith("/uploads/"));
    }

    @Test
    @DisplayName("이미지 파일이 null일 경우 예외 테스트")
    void uploadImageTests_imageFileEmpty_theowsException() {
        //given
        MockMultipartFile mockFile = null;

        //when & then
        CustomException exception = assertThrows(CustomException.class, () -> imageUploadService.uploadImage(mockFile));
        assertEquals(CustomErrorCode.INVALID_IMAGE_FILE, exception.getCustomErrorCode());
    }

    @Test
    @DisplayName("파일의 내용이 비어있는 경우 예외 테스트 ")
    void testUploadImage_emptyFile_throwsException() {
        //given
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.jpg",
                "image/jpeg",
                new byte[0]
        );

        //when & then
        CustomException exception = assertThrows(CustomException.class, () -> imageUploadService.uploadImage(emptyFile));
        assertEquals(CustomErrorCode.INVALID_IMAGE_FILE, exception.getCustomErrorCode());
    }




}
