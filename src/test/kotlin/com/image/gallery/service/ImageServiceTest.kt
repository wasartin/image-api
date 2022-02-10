package com.image.gallery.service

import com.image.gallery.integration.Image
import com.image.gallery.model.dao.Photo
import com.image.gallery.repository.PhotoRepository
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.io.File
import java.time.LocalDateTime

class ImageServiceTest {

    @MockkBean
    private lateinit var photoRepo: PhotoRepository

    @Autowired
    private lateinit var imageService: ImageService

    @BeforeEach
    fun setup(){
        every {
            photoRepo.save(any())
        } returns Photo(0, "", "", LocalDateTime.now())
    }

    @Test
    fun `when an image is saved to the database, it should extract the filename from the filepath`(){


        // send image to service

        // ensure the returned object has the correct filename
    }
}