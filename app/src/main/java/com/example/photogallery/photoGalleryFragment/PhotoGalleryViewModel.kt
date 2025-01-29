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
import kotlinx.coroutines.launch
import javax.inject.Inject

class PhotoGalleryViewModel @Inject constructor(
    private val pagerFetcher: PagerFetcher
): ViewModel() {

    private val _searchPhotoLD = MutableLiveData<PagingData<GalleryItem>>()
    val searchPhotoLD: LiveData<PagingData<GalleryItem>>
        get() = _searchPhotoLD

    init {
        Log.i(TAG, "view model is initialization ✅")
    }

    fun getPhoto(): LiveData<PagingData<GalleryItem>> {
        return pagerFetcher.getPagingPhoto().cachedIn(viewModelScope)
    }

    fun searchPhoto(text: String) {
        val result = pagerFetcher.searchPagingPhoto(text).cachedIn(viewModelScope)

        return result.observeForever {
            _searchPhotoLD.postValue(it)
        }
    }

    companion object {
        private const val TAG = "PhotoGalleryViewModel"
    }
}