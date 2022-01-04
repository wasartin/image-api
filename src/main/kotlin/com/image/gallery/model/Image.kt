package com.image.gallery.model

import java.awt.image.BufferedImage

class Image {
    var fileName : String = ""
    var directory : String = ""
    var img : BufferedImage? = null
    var orientation : Orientation? = null
}

enum class Orientation {
    PORTRAIT, LANDSCAPE
}