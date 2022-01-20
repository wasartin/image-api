package com.image.gallery.Integration

import com.beust.klaxon.Klaxon
import junit.framework.TestCase.assertNotNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.transaction.annotation.Transactional

@Transactional
@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EndPointTest {

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @Test
    fun `given the database is live, when there is a request for all photos, then it will return them`() {
        val result = testRestTemplate.getForEntity("/v1/photos", String::class.java)
        assertNotNull(result)
        assertEquals(result.statusCode, HttpStatus.OK)
    }

    @Test
    fun `given the database is live, when there is a request for an addition, it succeeds`(){
        val originalDBContents = testRestTemplate.getForEntity("/v1/photos", String::class.java)
        val originalDBSize = jsonToObjects(originalDBContents.body.toString()).size

        val newPhoto = Photo(0,"www.google.com", "something", "2022-01-19T02:52:52.841689")
        val result = testRestTemplate.postForEntity("/v1/photos", newPhoto, String::class.java)
        assertNotNull(result)
        assertEquals(result.statusCode, HttpStatus.OK)

        val updatedDBContents = testRestTemplate.getForEntity("/v1/photos", String::class.java)
        val updatedDBSize = jsonToObjects(updatedDBContents.body.toString()).size

        assertTrue(updatedDBSize > originalDBSize)
    }

    private fun jsonToObjects(json: String): List<Photo> {
        return Klaxon()
            .parseArray(json) ?: listOf()
    }

    private fun jsonToObject(json: String): Photo {
        return Klaxon().parse(json) ?: Photo()
    }

    @Test
    fun `given the database is live, where there is a request for a delete, it succeeds`(){
        val newPhoto = Photo(0,"someFilePath", "something", "2022-01-19T02:52:52.841689")
        val result = testRestTemplate.postForEntity("/v1/photos", newPhoto, String::class.java)
        val idToDelete = jsonToObject(result.body.toString()).photoId

        val originalDBContents = testRestTemplate.getForEntity("/v1/photos", String::class.java)
        val originalDBSize = jsonToObjects(originalDBContents.body.toString()).size

        testRestTemplate.delete("/v1/photos/{photoId}", idToDelete)

        val updatedDBContents = testRestTemplate.getForEntity("/v1/photos", String::class.java)
        val updatedDBSize = jsonToObjects(updatedDBContents.body.toString()).size

        assertTrue(updatedDBSize < originalDBSize)
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
