package com.image.gallery.utils


import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.image.BufferedImage
import java.io.File
import java.lang.Exception


class Transform(
    var directory: String
) {
    var logger: Logger = LoggerFactory.getLogger(Transform::class.java)
    // Given a directory
    // Turn files into images.
    fun createImage(filePath: String): BufferedImage? {
        val imageFile = File(filePath)
        try {
        } catch (e: Exception){
            logger.info("Couldn't process image, with file location = ${imageFile.absolutePath}")
        }
        return null
    }

}