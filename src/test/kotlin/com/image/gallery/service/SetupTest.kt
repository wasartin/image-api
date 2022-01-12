package com.image.gallery.service

import com.image.gallery.utils.Transform
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SetupTest {

    private val setup = Transform("../../../../../resources/static/posters")

    @Test
    fun `when given a valid file path, it should return an image`(){
        val validImageFilePath = ""
        val result = setup.createImage(validImageFilePath)
        assertTrue(result != null)
    }
}