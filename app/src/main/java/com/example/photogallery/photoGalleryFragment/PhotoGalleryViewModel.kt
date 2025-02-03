package com.example.photogallery.photoGalleryFragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photogallery.api.GalleryItem
import com.example.photogallery.data.FlickrFetcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class PhotoGalleryViewModel @Inject constructor(
    private val flickrFetcher: FlickrFetcher
): ViewModel() {

    private var galleryItems = MutableLiveData<List<GalleryItem>>()

    init {
        Log.i(TAG, "view model is initialization ✅")
    }

    fun getPhoto(): LiveData<List<GalleryItem>> {
        viewModelScope.launch(Dispatchers.IO) {
            galleryItems.postValue(flickrFetcher.fetchPhotos(1))
            Log.d(TAG, "getPhoto -> ${galleryItems.value}")
        }

        return galleryItems
    }

    fun searchPhoto(text: String): LiveData<List<GalleryItem>> {
        viewModelScope.launch(Dispatchers.IO) {
            galleryItems.postValue(flickrFetcher.searchPhotos(text))
            Log.d(TAG, "searchPhoto -> ${galleryItems.value}")
        }

        return galleryItems
    }

    fun loadingState(state: (Boolean) -> Unit) {
        viewModelScope.launch {
            flickrFetcher.isLoadingState.collect {

                if (it) {
                    state(true)
                } else {
                    state(false)
                }

            }
        }
    }

    companion object {
        private const val TAG = "PhotoGalleryViewModel"
    }
}