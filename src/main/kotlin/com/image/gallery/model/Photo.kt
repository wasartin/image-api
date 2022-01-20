package com.image.gallery.model

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "photo")
data class Photo (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="pho_id")
    val photoId: Int,

    @Column(name="pho_file_path")
    val filePath: String,

    @Column(name="pho_title")
    val title: String,

    @Column(name="pho_created")
    val created: LocalDateTime
)