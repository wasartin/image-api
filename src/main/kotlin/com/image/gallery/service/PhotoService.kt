package com.image.gallery.service

import com.image.gallery.model.Photo
import com.image.gallery.repository.PhotoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PhotoService(
    @Autowired private val repo: PhotoRepository
){

//    @Autowired
//    lateinit var photoRepo: PhotoRepository
    // get a photo ?? idk if i'll do this

    // get all photos
    fun getAll(): List<Photo> {
        return repo.findAll().toList()
    }

    // add a photo

    // remove a photo

}