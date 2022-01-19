package com.image.gallery.service

import com.image.gallery.model.Photo
import com.image.gallery.repository.PhotoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PhotoService(
    @Autowired private val repo: PhotoRepository
){
    // get all photos
    fun getAll(): List<Photo> {
        return repo.findAll().toList()
    }

    // remove a photo

}