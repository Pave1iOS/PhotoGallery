package com.example.photogallery.photoGalleryFragment

import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.photogallery.data.FlickrFetcher
import com.example.photogallery.data.ThumbnailDownloader
import javax.inject.Inject

class PhotoGalleryViewModel @Inject constructor(
    private val flickrFetcher: FlickrFetcher
): ViewModel() {

    val galleryItems = flickrFetcher.getPagingPhoto().cachedIn(viewModelScope)

    private val responseHandler = Handler(Looper.getMainLooper())
    private lateinit var thumbnailDownloader:
            ThumbnailDownloader<PhotoGalleryAdapter.PhotoGalleryViewHolder>

    private var pictureArray = mutableSetOf<String>()

    init {
        Log.i(TAG, "view model is initialization ✅")
        thumbnailDownloaderInitialize()

        thumbnailDownloader.startThread()
    }

    private fun thumbnailDownloaderInitialize() {

        thumbnailDownloader = ThumbnailDownloader(flickrFetcher, responseHandler) { holder, bitmap ->
            val drawable = BitmapDrawable(Resources.getSystem(), bitmap)
            holder.bindDrawable(drawable)
        }

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