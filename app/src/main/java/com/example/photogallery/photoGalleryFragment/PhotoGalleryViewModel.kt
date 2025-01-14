package com.example.photogallery.photoGalleryFragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.photogallery.api.FlickrApi
import com.example.photogallery.data.FlickrFetcher
import com.example.photogallery.data.FlickrPagingSource
import com.example.photogallery.data.GalleryItem
import javax.inject.Inject

class PhotoGalleryViewModel @Inject constructor(
    private val flickrFetcher: FlickrFetcher
): ViewModel() {

    val galleryItems: LiveData<PagingData<GalleryItem>> = flickrFetcher.getPagingPhoto()

    init {
        Log.i(TAG, "view model is initialization ✅")
    }

    fun fetchPhoto(page: Int) {
        flickrFetcher.fetchPhotos(page)
    }

    companion object {
        private const val TAG = "PhotoGalleryViewModel"
    }
}