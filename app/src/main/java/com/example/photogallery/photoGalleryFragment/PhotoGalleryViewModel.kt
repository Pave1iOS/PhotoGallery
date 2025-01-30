package com.example.photogallery.photoGalleryFragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.photogallery.api.GalleryItem
import com.example.photogallery.data.PagerFetcher
import javax.inject.Inject

class PhotoGalleryViewModel @Inject constructor(
    private val pagerFetcher: PagerFetcher
): ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    init {
        Log.i(TAG, "view model is initialization ✅")
    }

    fun getPhoto(): LiveData<PagingData<GalleryItem>> {
        _isLoading.value = true

        return pagerFetcher.getPagingPhoto().cachedIn(viewModelScope)
    }

    fun searchPhoto(text: String): LiveData<PagingData<GalleryItem>> {
        _isLoading.value

        return pagerFetcher.searchPagingPhoto(text).cachedIn(viewModelScope)
    }

    companion object {
        private const val TAG = "PhotoGalleryViewModel"
    }
}