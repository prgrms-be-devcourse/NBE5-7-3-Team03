package com.team573.gongguri.domain.grouppurchase.controller

import com.team573.gongguri.domain.grouppurchase.service.ImageUploadService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/images")
class ImageUploadController(
    private val imageUploadService: ImageUploadService
) {
    @PostMapping(consumes = ["multipart/form-data"])
    fun uploadImage(@RequestPart("imageFile") file: MultipartFile?): ResponseEntity<Map<String, String>> {
        val response = imageUploadService.uploadImage(file)
        return ResponseEntity.ok(response)
    }
}
