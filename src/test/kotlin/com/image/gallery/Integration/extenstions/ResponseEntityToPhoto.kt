package com.image.gallery.integration.extenstions

import com.beust.klaxon.Klaxon
import com.image.gallery.integration.Photo
import org.springframework.http.ResponseEntity

fun ResponseEntity<String>.jsonToObject() : Photo {
    val contents = this.body.toString()
    return Klaxon()
        .parse(contents) ?: Photo()
}

fun ResponseEntity<String>.jsonToObjects() : List<Photo> {
    val contents = this.body.toString()
    return Klaxon()
        .parseArray(contents) ?: listOf()
}