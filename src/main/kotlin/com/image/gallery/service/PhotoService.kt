package com.image.gallery.service

import com.image.gallery.model.dao.Photo
import com.image.gallery.model.dto.Image
import com.image.gallery.repository.PhotoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import java.io.File
import java.time.LocalDateTime
import java.util.*


@Service
class PhotoService(
    @Autowired private val repo: PhotoRepository,
    @Value("\${image.directory}") private val imageDirectory: String
){
    fun getAll(): List<Photo> {
        return repo.findAll().toList()
    }

    fun add(newPhoto: Photo) : Photo {
        return repo.save(newPhoto)
    }

    //TODO: This is horrible, I know.
    fun savePhoto(base64Photo: String, title: String): ResponseEntity<Photo> {
        val photo = createPhoto(title)
        var status = HttpStatus.INTERNAL_SERVER_ERROR
        try {
            val savedPhoto = repo.save(photo)
            saveImageToStorage(savedPhoto.title, base64Photo)
            return getResponseEntity(status, savedPhoto)
        } catch(e: Exception) {
            status = HttpStatus.BAD_REQUEST
        }
        return getResponseEntity(status, photo)

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

    private fun createPhoto(title : String): Photo {
        val filePath = "$imageDirectory$title"
        return Photo(
            photoId = -100,
            filePath = filePath,
            title = title,
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

    //TODO: Horrible, I know
    private fun getResponseEntity(status: HttpStatus, payload: Photo): ResponseEntity<Photo> {
        // Figure out how to inject headers
        // probably just HttpHeaders.
        val headers = hashMapOf(
            "Content-Type" to "application/json",
            "Access-Control-Allow-Origin" to "*"
        )
        return ResponseEntity<Photo>(payload, status)
    }
}