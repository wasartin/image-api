package com.image.gallery.repository

import com.image.gallery.model.Photo
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository

@Repository
interface PhotoRepository : CrudRepository<Photo, Int>