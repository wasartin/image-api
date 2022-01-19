package com.image.gallery.service

import com.image.gallery.repository.PhotoRepository
import junit.framework.TestCase.assertNotNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EndPointTest {
//
//    @Autowired
//    lateinit var testRestTemplate: TestRestTemplate
//
//    @Test
//    fun testController() {
//        val result = testRestTemplate.getForEntity("/photo/all", String::class.java)
//        assertNotNull(result)
//        assertEquals(result.statusCode, HttpStatus.OK)
//        //assertEquals(result.body, "Hello string!")
//    }
}