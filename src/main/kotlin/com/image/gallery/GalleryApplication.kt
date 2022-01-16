package com.image.gallery

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableJpaRepositories
@EntityScan("com.image.gallery")
@ComponentScan("com.image.gallery")
class GalleryApplication
fun main(args: Array<String>) {
	runApplication<GalleryApplication>(*args)
}
