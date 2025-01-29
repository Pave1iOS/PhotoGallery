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

class PhotoGalleryViewModel @Inject constructor(
    private val flickrFetcher: FlickrFetcher
): ViewModel() {

    init {
        Log.i(TAG, "view model is initialization ✅")
    }

    fun getPhoto(): LiveData<PagingData<GalleryItem>> {
        return flickrFetcher.getPagingPhoto().cachedIn(viewModelScope)
    }

    fun searchPhoto(text: String): LiveData<PagingData<GalleryItem>> {
        return flickrFetcher.searchPagingPhoto(text).cachedIn(viewModelScope)
    }

    companion object {
        private const val TAG = "PhotoGalleryViewModel"
    }
}