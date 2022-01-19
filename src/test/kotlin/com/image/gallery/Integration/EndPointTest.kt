package com.image.gallery.Integration

import com.beust.klaxon.Klaxon
import junit.framework.TestCase.assertNotNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EndPointTest {

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @Test
    fun `given the database is live, when there is a request for all photos, then it will return them`() {
        val result = testRestTemplate.getForEntity("/photo/all", String::class.java)
        assertNotNull(result)
        assertEquals(result.statusCode, HttpStatus.OK)
    }



    @Test
    fun `given the database is live, when there is a request for an addition, it succeeds`(){
        val originalDBContents = testRestTemplate.getForEntity("/photo/all", String::class.java)
        val originalDBSize = jsonToObjects(originalDBContents.body.toString())
        // hit the add endpoint correctly

        //testRestTemplate.postForEntity("/photo/add", """""")

        // get all DB contents again

        // ensure it is no bigger than the previous DB Contents
    }

    private fun jsonToObjects(json: String): List<Photo> {
        return Klaxon()
            .parseArray(json) ?: listOf()
    }
}

/**
 * To simulate the Photo Entity
 */
class Photo(
    val photoId: Int = 0,
    val filePath: String = "",
    val title: String = "",
    val created: String = ""
)
