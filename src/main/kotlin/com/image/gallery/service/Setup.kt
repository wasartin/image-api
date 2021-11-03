package com.image.gallery.service


import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.image.BufferedImage
import java.io.File
import java.lang.Exception
import javax.swing.text.html.ImageView


class Setup(
    var directory: String
) {
    var logger: Logger = LoggerFactory.getLogger(Setup::class.java)
    // Given a directory
    // Turn files into images.
    fun createImage(filePath: String): BufferedImage? {
        val imageFile = File(filePath)
        try {
            var imgView : ImageView = ImageView(imageFile!!)
        } catch (e: Exception){
            logger.info("Couldn't process image, with file location = ${imageFile.absolutePath}")
            throw e
        }
        return null
    }

}