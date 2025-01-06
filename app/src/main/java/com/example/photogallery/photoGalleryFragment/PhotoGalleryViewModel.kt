package com.example.photogallery.photoGalleryFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.photogallery.FlickrFetcher
import com.example.photogallery.GalleryItem

class PhotoGalleryViewModel: ViewModel() {

    val galleryItemLD: LiveData<List<GalleryItem>>

    init {
        galleryItemLD = FlickrFetcher().fetchPhotos()
    }
}