package com.example.photogallery.photoGalleryFragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.photogallery.data.FlickrFetcher
import com.example.photogallery.data.GalleryItem
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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