package com.example.photogallery.photoGalleryFragment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.photogallery.data.FlickrFetcher
import javax.inject.Inject

class PhotoGalleryViewModel @Inject constructor(
    flickrFetcher: FlickrFetcher
): ViewModel() {

    val galleryItems = flickrFetcher.getPagingPhoto().cachedIn(viewModelScope)

    init {
        Log.i(TAG, "view model is initialization ✅")
    }

    companion object {
        private const val TAG = "PhotoGalleryViewModel"
    }
}