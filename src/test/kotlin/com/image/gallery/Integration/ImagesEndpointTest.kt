package com.image.gallery.integration

import com.image.gallery.integration.extenstions.jsonToObject
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.transaction.annotation.Transactional
import java.io.File

import java.util.*

@Transactional
@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ImagesEndpointTest(
    @Value("\${image.directory}") private val tempDirectory: String
) {

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @AfterEach
    fun cleanTempDirectory(){
        val tempDirectory = File(tempDirectory)
        for (file in tempDirectory.listFiles()) {
            if (!file.isDirectory) {
                file.delete()
            }
        }
    }

    @Test
    fun `should create a unique id for the image`(){
        //given: a new image
        val imageDTO = defaultImage()

        //when: it is sent to the rest api
        val result = testRestTemplate.postForEntity("/v1/images", imageDTO, String::class.java)
        val body = (result.body as String).jsonToObject()

        // then: it should have an updated photoID
        Assertions.assertEquals(HttpStatus.OK, result.statusCode)
        Assertions.assertTrue(body.id != imageDTO.id)
    }

    @Test
    fun `should save image`(){
        //given: a new image
        val imageDTO = defaultImage()

        //when: it is sent to the rest api
        val result = testRestTemplate.postForEntity("/v1/images", imageDTO, String::class.java)

        // then: the image should be saved to
        Assertions.assertEquals(HttpStatus.OK, result.statusCode)
        val savedFile = File(File(tempDirectory), "theThing_1982.jpg").exists()
        Assertions.assertTrue(savedFile)
    }

    @Test
    fun `should delete image`(){
        //given the file already exists on the machine
        val imageDTO = defaultImage()
        val result = testRestTemplate.postForEntity("/v1/images", imageDTO, String::class.java)
        val id = (result.body as String).jsonToObject().id

        //when: a request is made to delete the image
        testRestTemplate.delete("/v1/images/$id")
        // then: the image should no longer exist
        val nonExistentFile = File(File(tempDirectory), "theThing_1982.jpg").exists()
        Assertions.assertFalse(nonExistentFile)
    }


    private fun ByteArray.encodeToBase64(): String {
        return Base64.getEncoder().encodeToString(this)
    }


    private fun defaultImage(): Image {
        val filePath = "/Users/wsartin/dev/workshop/piFrame/pi-gallery/src/test/resources/static/posters/theThing_1982.jpg"
        val imageContents = File(filePath).readBytes()
        val encoded64 = imageContents.encodeToBase64()

        return Image(
            id = -100,
            filePath = filePath,
            contentsAsBase64 = encoded64,
        )
    }

}

class Image(
    var id: Int = -100,
    var filePath: String = "",
    var contentsAsBase64: String = "",
)

