package com.image.gallery.controller

import com.image.gallery.model.dto.Image
import com.image.gallery.service.ImageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
// TODO: Delete
@RestController
@RequestMapping("/v1/images")
class ImageController(
    @Autowired private val service: ImageService
){
    @PostMapping()
    fun addImage(@RequestBody newImage : Image): Image {
        return service.add(newImage)
    }

    @DeleteMapping("/{imageId}")
    fun deleteImage(@PathVariable imageId: Int) {
        service.delete(imageId)
    }

}