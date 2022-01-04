package com.image.gallery.service


import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.image.BufferedImage
import java.io.File
import java.lang.Exception


class Utils(
    var directory: String
) {
    var logger: Logger = LoggerFactory.getLogger(Utils::class.java)
    // Given a directory
    // Turn files into images.
    fun createImage(filePath: String): BufferedImage? {
        val imageFile = File(filePath)
        try {
        } catch (e: Exception){
            logger.info("Couldn't process image, with file location = ${imageFile.absolutePath}")
            throw e
        }
        return null
    }

}