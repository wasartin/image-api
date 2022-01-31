package com.image.gallery.utils

import com.image.gallery.model.dao.Photo
import com.image.gallery.model.dto.Image
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class Transform() {
    val logger: Logger = LoggerFactory.getLogger(Transform::class.java)

    fun createImage(photo: Photo): Image {

        return Image()
    }

}