package com.image.gallery

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GalleryApplication

fun main(args: Array<String>) {
	// Given a directory, and an interval
	// load files from given directory
	// turn files into Images
	// Display Images for interval
	runApplication<GalleryApplication>(*args)
}
