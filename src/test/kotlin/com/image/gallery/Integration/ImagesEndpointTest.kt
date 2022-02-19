package com.image.gallery.integration

import com.image.gallery.integration.extenstions.jsonToObject
import com.image.gallery.integration.extenstions.jsonToObjects
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.transaction.annotation.Transactional
import java.io.File

import java.math.BigDecimal
import java.util.*

@Transactional
@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ImagesEndpointTest {

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @Test
    fun `should create a unique id for the image`(){
        //given: a new image
        val imageDTO = Image(
            id = -100,
            filePath= "",
            contentsAsBase64 = "",
        )

        //when: it is sent to the rest api
        val result = testRestTemplate.postForEntity("/v1/images", imageDTO, String::class.java)
        val body = (result.body as String).jsonToObject()

        // then: we it should have an updated photoID
        Assertions.assertEquals(result.statusCode, HttpStatus.OK)
        Assertions.assertTrue(body.id != imageDTO.id)
    }

    @Test
    fun `should save image`(){

        val imageContents = File("/Users/wsartin/dev/workshop/piFrame/pi-gallery/src/test/resources/static/posters/theThing_1982.jpg")
            .readBytes()

        val encoded64 = imageContents.encodeToBase64()

        //given: a new image
        val imageDTO = Image(
            id = -100,
            filePath= "",
            contentsAsBase64 = encoded64,
        )

        //when: it is sent to the rest api
        val result = testRestTemplate.postForEntity("/v1/images", imageDTO, String::class.java)
        val body = (result.body as String).jsonToObject()

        // then: we it should have an updated photoID
        Assertions.assertEquals(result.statusCode, HttpStatus.OK)
        Assertions.assertTrue(body.id != imageDTO.id)
    }

    fun ByteArray.encodeToBase64(): String {
        return Base64.getEncoder().encodeToString(this)
    }

}

class Image(
    var id: Int = -100,
    var filePath: String = "",
    var contentsAsBase64: String = "",
)

