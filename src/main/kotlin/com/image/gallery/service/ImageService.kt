package com.image.gallery.service

import com.image.gallery.model.dao.Photo
import com.image.gallery.model.dto.Image
import java.time.LocalDateTime
import com.image.gallery.repository.PhotoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ImageService(
    @Autowired private val repo: PhotoRepository
){

//    fun delete(imageId: Int) {
//
//    }

    fun add(newImage: Image): Image {
        println("service time")
        val photo = createPhoto(newImage)
        val savedPhoto = repo.save(photo)
        newImage.id = savedPhoto.photoId
        return newImage
    }

    private fun createPhoto(image: Image): Photo {
        val filePathArray = image.filePath.split('/')
        val name : String  = filePathArray[filePathArray.lastIndex]
        return Photo(
            photoId = -100,
            filePath = image.filePath,
            title = name,
            created = LocalDateTime.now()
        )
    }

}