package com.image.gallery.controller

import com.image.gallery.service.PhotoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/photo")
class PhotoController(
    @Autowired private val service: PhotoService
){
//    @Autowired
//    lateinit var photoService: PhotoService

    @GetMapping("/all")
    fun getAllPhotos() = service.getAll()
}