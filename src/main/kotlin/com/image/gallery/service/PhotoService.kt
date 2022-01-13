package com.image.gallery.service

import com.image.gallery.dao.Photo
import com.image.gallery.repository.PhotoRepository
import org.springframework.beans.factory.annotation.Autowired

class PhotoService(
    @Autowired val photoRepo: PhotoRepository
) {

    // get a photo ?? idk if i'll do this

    // get all photos
    fun getAll(): List<Photo> {
        return photoRepo.findAll().toList()
    }

    // add a photo

    // remove a photo

}