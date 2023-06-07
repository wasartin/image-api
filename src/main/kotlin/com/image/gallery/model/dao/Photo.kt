package com.image.gallery.model.dao

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "image")
data class Photo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="img_id")
    val photoId: Int,

    @Column(name="img_file_path")
    val filePath: String,

    @Column(name="img_title")
    val title: String,

    @Column(name="img_author")
    val author: String,

    @Column(name="img_tags")
    val tags: Array<String>,

    @Column(name="img_added")
    val created: LocalDateTime
)