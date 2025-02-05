package com.example.photogallery.photoGalleryFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSourceFactory
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.example.photogallery.api.GalleryItem
import com.example.photogallery.data.FlickrFetcher
import com.example.photogallery.data.FlickrPagingSource
import kotlinx.coroutines.launch
import javax.inject.Inject

class PhotoGalleryViewModel @Inject constructor(
    private val flickrFetcher: FlickrFetcher
): ViewModel() {

    fun loadPhotos(): LiveData<PagingData<GalleryItem>> {
        val config = PagingConfig(PAGE_SIZE)

        val factory = PagingSourceFactory {
            FlickrPagingSource { page ->
                flickrFetcher.fetchPhotos(page)
            }
        }

        return Pager(config, pagingSourceFactory = factory)
            .liveData
            .cachedIn(viewModelScope)
    }

    fun searchPhotos(text: String): LiveData<PagingData<GalleryItem>> {
        val config = PagingConfig(PAGE_SIZE)

        val factory = PagingSourceFactory {
            FlickrPagingSource {
                flickrFetcher.searchPhotos(text)
            }
        }

        return Pager(config, pagingSourceFactory = factory)
            .liveData
            .cachedIn(viewModelScope)
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
        private const val PAGE_SIZE = 100
        private const val MODULE_NAME = "VIEW MODEL ->"
        private const val TAG = "PhotoGalleryViewModel"
    }
}