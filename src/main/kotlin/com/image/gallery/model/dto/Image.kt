package com.image.gallery.model.dto

data class Image(
    var id: Int = -100,
    var filePath: String = "",
    var contents: ByteArray? = null
)
