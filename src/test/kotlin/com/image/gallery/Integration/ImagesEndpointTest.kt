package com.image.gallery.integration

import com.image.gallery.integration.extenstions.jsonToObjects
import junit.framework.TestCase
import org.junit.jupiter.api.Assertions
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
class ImagesEndpointTest {

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @Test
    fun `given the database is live, when there is a request for an add to the Images Endpoint, it succeeds and returns the photoId`(){
        val originalDBContents = testRestTemplate.getForEntity("/v1/images", String::class.java)
        val originalDBSize = originalDBContents.jsonToObjects().size

        //send a photo

        //verify we received the photoId

        //check to see if that photoId is in the Database

        //Profit

//        val newPhoto = Photo(0,"www.google.com", "something", "2022-01-19T02:52:52.841689")
//        val result = testRestTemplate.postForEntity("/v1/photos", newPhoto, String::class.java)
//        TestCase.assertNotNull(result)
//        Assertions.assertEquals(result.statusCode, HttpStatus.OK)
//
//        val updatedDBContents = testRestTemplate.getForEntity("/v1/photos", String::class.java)
//        val updatedDBSize = updatedDBContents.jsonToObjects().size
//
//        Assertions.assertTrue(updatedDBSize > originalDBSize)
    }
}