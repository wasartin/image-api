package com.image.gallery.dao

import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "photo")
data class Photo (
    @Id
    @Column(name="pho_id")
    val photoId: Int,

    @Column(name="pho_file_path")
    val filePath: String,

    @Column(name="pho_title")
    val title: String,

    @Column(name="pho_created")
    val created: LocalDateTime
)