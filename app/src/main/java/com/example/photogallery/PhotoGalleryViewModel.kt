package com.example.photogallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class PhotoGalleryViewModel: ViewModel() {

    val galleryItemLD: LiveData<List<GalleryItem>>

    init {
        galleryItemLD = FlickrFetcher().fetchPhotos()
    }
}