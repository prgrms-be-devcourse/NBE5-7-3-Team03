package com.team573.gongguri.domain.grouppurchase.service

import com.team573.gongguri.global.exception.CustomErrorCode
import com.team573.gongguri.global.exception.CustomException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import java.util.*

@Service
class ImageUploadService {
    fun uploadImage(file: MultipartFile?): Map<String, String> {
        if (file == null ||file.isEmpty) {
            throw CustomException(CustomErrorCode.INVALID_IMAGE_FILE)
        }

        val filename = UUID.randomUUID().toString() + "_" + file.originalFilename

        val uploadPath = File(UPLOAD_DIR)
        if (!uploadPath.exists()) {
            val created = uploadPath.mkdirs()
            log.info("업로드 폴더 생성됨: {}", created)
        }

        val dest = File(uploadPath, filename)
        try {
            file.transferTo(dest)
        } catch (e: IOException) {
            log.error("파일 저장 실패", e)
            throw CustomException(CustomErrorCode.IMAGE_UPLOAD_FAILED)
        }

        val imageUrl = "/uploads/$filename"
        log.info("이미지 저장 완료: {}", dest.absolutePath)

        val response = mutableMapOf("imageUrl" to imageUrl)
        return response
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(ImageUploadService::class.java)
        private val UPLOAD_DIR = System.getProperty("user.home") + "/gongguri-uploads"
    }
}
