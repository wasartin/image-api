package com.image.gallery.service

import com.image.gallery.repository.PhotoRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PhotoServiceTest {

    @InjectMocks
    private lateinit var photoRepository: PhotoRepository

    @Mock
    private var photoService = PhotoService()

    @Test
    fun `given a perfect world, when a request is made for all photos, then all photos should be returned`(){

        val result = photoService.getAll()
        assertEquals(2, result.size)
    }
}