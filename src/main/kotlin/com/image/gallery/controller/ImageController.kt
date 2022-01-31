package com.image.gallery.controller

import com.image.gallery.model.dao.Photo
import com.image.gallery.model.dto.Image
import com.image.gallery.service.ImageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/images")
class ImageController(
    @Autowired private val service: ImageService
){
    @PostMapping()
    fun addImage(@RequestBody newImage : Image): Image {
        return service.add(newImage)
    }

//    @DeleteMapping("/{imageId}")
//    fun deletePhotoById(@PathVariable imageId : Int) {
//        return service.delete(imageId)
//    }
}