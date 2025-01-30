package com.example.photogallery.data

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.photogallery.api.GalleryItem
import javax.inject.Inject

class PagerFetcher @Inject constructor(
    private val flickrFetcher: FlickrFetcher
) {

    fun getPagingPhoto(): LiveData<PagingData<GalleryItem>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE),
            pagingSourceFactory = { FlickrPagingSource { page -> flickrFetcher.fetchPhotos(page) } }
        ).liveData
    }

    fun searchPagingPhoto(text: String): LiveData<PagingData<GalleryItem>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE),
            pagingSourceFactory = { FlickrPagingSource { flickrFetcher.searchPhotos(text) } }
        ).liveData
    }

    companion object {
        private const val TAG = "PagerFetcher"
        private const val PAGE_SIZE = 100
    }

}