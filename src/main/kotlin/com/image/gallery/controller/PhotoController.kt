package com.image.gallery.controller

import com.image.gallery.model.dao.Photo
import com.image.gallery.service.PhotoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/photos")
class PhotoController(
    @Autowired private val service: PhotoService
){
    @GetMapping()
    fun getAllPhotos() = service.getAll()

    @PostMapping()
    fun addPhoto(@RequestBody newPhoto : Photo): Photo {
        return service.add(newPhoto)
    }

    @PostMapping()
    fun addPhoto(@RequestBody photoAsBase64 : String): ResponseEntity<Photo> {
        return service.savePhoto(photoAsBase64, "unknown")
    }

    @DeleteMapping("/{photoId}")
    fun deletePhotoById(@PathVariable photoId : Int): Boolean {
        return service.delete(photoId)
    }
}