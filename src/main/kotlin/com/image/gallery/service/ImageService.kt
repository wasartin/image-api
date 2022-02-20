package com.image.gallery.service

import com.image.gallery.model.dao.Photo
import com.image.gallery.model.dto.Image
import java.time.LocalDateTime
import com.image.gallery.repository.PhotoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.util.*

@Service
class ImageService(
    @Autowired private val repo: PhotoRepository,
    @Value("\${image.directory}") private val imageDirectory: String
){
    fun add(newImage: Image): Image {
        val photo = createPhoto(newImage)
        val savedPhoto = repo.save(photo)
        newImage.id = savedPhoto.photoId
        saveImageToStorage(savedPhoto.title, newImage.contentsAsBase64)
        return newImage
    }

    private fun saveImageToStorage(name: String, imageContents: String): String{
        val folderDir = File(imageDirectory)
        val imageFile = File(folderDir, name)
        imageFile.parentFile.mkdirs()
        imageFile.writeBytes(imageContents.fromBase64ToByteArray())
        return imageFile.absolutePath
    }

    private fun String.fromBase64ToByteArray(): ByteArray {
        return Base64.getDecoder().decode(this)
    }

    private fun createPhoto(image: Image): Photo {
        val filePathArray = image.filePath.split('/')
        val name : String  = filePathArray[filePathArray.lastIndex]
        val filePath = "$imageDirectory$name"
        return Photo(
            photoId = -100,
            filePath = filePath,
            title = name,
            created = LocalDateTime.now()
        )
    }

    fun delete(imageId: Int): Boolean {
        val imageLocation = repo.getById(imageId).filePath
        repo.deleteById(imageId)
        return deleteImageFromStorage(imageLocation)
    }

    private fun deleteImageFromStorage(filePath: String): Boolean {
        val fileToDelete = File(filePath)
        return fileToDelete.delete()
    }
}