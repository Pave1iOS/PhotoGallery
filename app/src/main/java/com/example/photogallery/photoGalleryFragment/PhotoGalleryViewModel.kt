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

    private var _galleryItems = MutableLiveData<List<GalleryItem>>()
    val galleryItems: LiveData<List<GalleryItem>> get() = _galleryItems

    private var page = 1

    fun loadPhotos(text: String = ""): LiveData<List<GalleryItem>> {
        viewModelScope.launch {
            try {
                val newPhoto = if (text.isBlank()) {
                    flickrFetcher.fetchPhotos(page)
                } else {
                    flickrFetcher.searchPhotos(text)
                }

                if (page == 1) {
                    _galleryItems.value = newPhoto
                } else {
                    _galleryItems.value = (_galleryItems.value ?: emptyList()) + newPhoto
                }

                page ++

                Log.i(TAG, "photos -> ${_galleryItems.value}\n" +
                        "page $page") // сюда сцену ошибки

            } catch (e: Exception) {
                Log.e(TAG, "🔴Filed to load photo")
                throw e
            }
        }

        return _galleryItems
    }

    fun searchPhotos(text: String): LiveData<List<GalleryItem>> {

        page = 1
        _galleryItems.value = emptyList()

        return loadPhotos(text)
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