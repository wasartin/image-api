package com.image.gallery.controller

import com.image.gallery.service.PhotoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/photo")
class PhotoController(
    @Autowired private val service: PhotoService
){

    @GetMapping("/all")
    fun getAllPhotos() = service.getAll()
}