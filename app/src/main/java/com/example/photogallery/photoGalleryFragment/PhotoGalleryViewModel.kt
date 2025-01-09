package com.example.photogallery.photoGalleryFragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.photogallery.data.FlickrFetcher
import com.example.photogallery.data.GalleryItem
import javax.inject.Inject

class PhotoGalleryViewModel @Inject constructor(flickrFetcher: FlickrFetcher): ViewModel() {

    val galleryItemLD: LiveData<List<GalleryItem>> = flickrFetcher.fetchPhotos()

    init {
        Log.d(TAG, "viewModel init")
    }

    override fun onCleared() {
        super.onCleared()

        Log.d(TAG, "view model is destroy")
    }

    companion object {
        private const val TAG = "PhotoGalleryViewModel"
    }
}