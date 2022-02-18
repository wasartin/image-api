package com.image.gallery.service

import com.image.gallery.model.dao.Photo
import com.image.gallery.model.dto.Image
import com.image.gallery.repository.PhotoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class ImageService(
    @Autowired private val repo: PhotoRepository,
    @Value("image.directory") private val directory: String
){
    fun add(newImage: Image): Image {
        println("service time")
        val photo = createPhoto(newImage)
        val savedPhoto = repo.save(photo)
        newImage.id = savedPhoto.photoId
        //write the image to a location
        saveImageByte(newImage)
        return newImage
    }

    private fun saveImageByte(image: Image) {

    }

//    private fun deleteImageByte(imageId: Int){
//        redisTemplate.delete(imageId)
//    }

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