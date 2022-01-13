package com.image.gallery.repository

import com.image.gallery.dao.Photo
import org.springframework.data.repository.CrudRepository;

interface PhotoRepository : CrudRepository<Photo, Int>  {

}