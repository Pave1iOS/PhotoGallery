package com.example.photogallery.photoGalleryFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.photogallery.api.FlickrApi
import com.example.photogallery.data.FlickrFetcher
import com.example.photogallery.data.GalleryItem
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PhotoGalleryViewModel: ViewModel() {

    val galleryItemLD: LiveData<List<GalleryItem>>

    init {
        galleryItemLD = FlickrFetcher(initializeFlickrAPI()).fetchPhotos()
    }

    private fun initializeFlickrAPI(): FlickrApi {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.flickr.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(FlickrApi::class.java)
    }
}