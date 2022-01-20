package com.image.gallery.service

import com.image.gallery.model.Photo
import com.image.gallery.repository.PhotoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PhotoService(
    @Autowired private val repo: PhotoRepository
){
    fun getAll(): List<Photo> {
        return repo.findAll().toList()
    }

    fun add(newPhoto: Photo) : Photo {
        return repo.save(newPhoto)
    }

    fun delete(photoId: Int) {
        return repo.deleteById(photoId)
    }

}