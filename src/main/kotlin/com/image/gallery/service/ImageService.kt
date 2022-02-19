package com.image.gallery.service

import com.image.gallery.model.dao.Photo
import com.image.gallery.model.dto.Image
import java.time.LocalDateTime
import com.image.gallery.repository.PhotoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.util.*

@Service
class ImageService(
    @Autowired private val repo: PhotoRepository,
    @Value("\${image.directory}") private val imageDirectory: String
){
    fun add(newImage: Image): Image {
        println("service time")
        val photo = createPhoto(newImage)
        val savedPhoto = repo.save(photo)
        newImage.id = savedPhoto.photoId
        return newImage
    }

    private fun savePhoto(name: String, image: Image){
        val folderDir = File(imageDirectory)
        val imageFile = File(folderDir, name)
        imageFile.parentFile.mkdirs()
        imageFile.writeBytes(image.contentsAsBase64.fromBase64ToByteArray())
    }

    private fun String.fromBase64ToByteArray(): ByteArray {
        return Base64.getDecoder().decode(this)
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