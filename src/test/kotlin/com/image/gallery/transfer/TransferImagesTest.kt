package com.image.gallery.transfer

import org.junit.jupiter.api.Test
import java.io.File
import java.util.*

class TransferImagesTest {


    fun String.decodeFromBase64(): ByteArray {
        return Base64.getDecoder().decode(this)
    }

    fun ByteArray.encodeToBase64(): String {
        return Base64.getEncoder().encodeToString(this)
    }

    @Test
    fun `temp test to see if I can encode`(){
        val imageContents = File("/Users/wsartin/dev/workshop/piFrame/pi-gallery/src/test/resources/static/posters/theThing_1982.jpg")
            .readBytes()

        val encoded64 = imageContents.encodeToBase64()
        val len1 = imageContents.toString().length
        val len2 = encoded64.length

        println (len1)
        println(len2) //seems really long, 1.3m chars.
        println(encoded64)
    }

    @Test
    fun `protocol buffer sizes`(){
        val imageContents = File("/Users/wsartin/dev/workshop/piFrame/pi-gallery/src/test/resources/static/posters/theThing_1982.jpg")
            .readBytes()
    }


    // ideas
    // send the base64 string

    // idea2
    // send the bytes

    // idea3
    // partition the image contents

}