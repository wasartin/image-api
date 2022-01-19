package com.image.gallery.repository

import com.image.gallery.model.Photo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PhotoRepository : JpaRepository<Photo, Int>