package com.example.photogallery.photoGalleryFragment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.photogallery.data.FlickrFetcher
import com.example.photogallery.data.ThumbnailDownloader
import javax.inject.Inject

class PhotoGalleryViewModel @Inject constructor(
    flickrFetcher: FlickrFetcher
): ViewModel() {

    val galleryItems = flickrFetcher.getPagingPhoto().cachedIn(viewModelScope)

    private var thumbnailDownloader: ThumbnailDownloader<PhotoGalleryAdapter.PhotoGalleryViewHolder> =
        ThumbnailDownloader(flickrFetcher)

    private var pictureArray = mutableSetOf<String>()

    init {
        Log.i(TAG, "view model is initialization ✅")
        thumbnailDownloader.startThread()
    }

    fun downloadPicture(holder: PhotoGalleryAdapter.PhotoGalleryViewHolder, url: String) {
        if (!pictureArray.contains(url)) {
            thumbnailDownloader.queueThumbnail(holder, url)
            pictureArray.add(url)
        }
    }

    override fun onCleared() {
        super.onCleared()
        thumbnailDownloader.stopThread()
    }

    companion object {
        private const val TAG = "PhotoGalleryViewModel"
    }
}