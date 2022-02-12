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
            contents = "",
        )
        //

        //when: it is sent to the rest api
        val result = testRestTemplate.postForEntity("/v1/images", imageDTO, String::class.java)
        val body = (result.body as String).jsonToObject()

        // then: we it should have an updated photoID
        Assertions.assertEquals(result.statusCode, HttpStatus.OK)
        Assertions.assertTrue(body.id != imageDTO.id)
    }
}

class Image(
    var id: Int = -100,
    var filePath: String = "",
    var contents: String = "",
)

