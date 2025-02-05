package com.example.photogallery.photoGalleryFragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photogallery.api.GalleryItem
import com.example.photogallery.data.FlickrFetcher
import kotlinx.coroutines.launch
import javax.inject.Inject

class PhotoGalleryViewModel @Inject constructor(
    private val flickrFetcher: FlickrFetcher
): ViewModel() {

    private var _galleryItems = MutableLiveData<List<GalleryItem>>()
    val galleryItems: LiveData<List<GalleryItem>> get() = _galleryItems

    var page = 0

    fun loadPhotos(text: String = ""): LiveData<List<GalleryItem>> {

        page ++

        viewModelScope.launch {
            try {

                val newPhoto = if (text.isBlank()) {
                    flickrFetcher.fetchPhotos(page)
                } else {
                    _galleryItems.value = emptyList()
                    page = 1
                    flickrFetcher.searchPhotos(text)
                }

                if (page == 1) {
                    _galleryItems.value = newPhoto
                } else {
                    _galleryItems.value = (_galleryItems.value ?: emptyList()) + newPhoto
                }


                Log.i(TAG, "🟢$MODULE_NAME upload is caused (page $page)")

            } catch (e: Exception) {
                Log.e(TAG, "🔴$MODULE_NAME Filed to load photo")
                throw e
            }
        }

        return _galleryItems
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
        private const val MODULE_NAME = "VIEW MODEL ->"
        private const val TAG = "PhotoGalleryViewModel"
    }
}