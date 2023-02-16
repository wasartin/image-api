package com.image.gallery

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GalleryApplication
fun main(args: Array<String>) {
	// TODO: Get location to save photos
	runApplication<GalleryApplication>(*args)
}
