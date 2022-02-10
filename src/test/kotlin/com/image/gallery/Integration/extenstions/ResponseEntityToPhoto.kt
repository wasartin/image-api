package com.image.gallery.integration.extenstions

import com.beust.klaxon.Klaxon
import com.image.gallery.integration.Image
import com.image.gallery.integration.Photo
import org.springframework.http.ResponseEntity
import java.time.LocalDateTime

fun ResponseEntity<String>.jsonToObject() : Photo {
    val contents = this.body.toString()
    return Klaxon()
        .parse(contents) ?: Photo(-100, "", "", "")
}

fun ResponseEntity<String>.jsonToObjects() : List<Photo> {
    val contents = this.body.toString()
    return Klaxon()
        .parseArray(contents) ?: listOf()
}

fun String.jsonToObject() : Image {
    return Klaxon()
        .parse(this) ?: Image(-100, "", "")
}