package com.image.gallery.controller

import com.image.gallery.model.Photo
import com.image.gallery.service.PhotoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/photos")
class PhotoController(
    @Autowired private val service: PhotoService
){
    @GetMapping()
    fun getAllPhotos() = service.getAll()

    @PostMapping()
    fun addPhoto(@RequestBody newPhoto : Photo): Photo {
        return service.add(newPhoto)
    }
}