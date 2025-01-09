package com.example.photogallery.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.photogallery.api.FlickrApi
import com.example.photogallery.api.FlickrResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class FlickrFetcher @Inject constructor(private var flickrApi: FlickrApi) {

    fun fetchPhotos(): LiveData<List<GalleryItem>> {
        val responseLD = MutableLiveData<List<GalleryItem>>()
        val flickrRequest = flickrApi.fetchPhotos()

        flickrRequest.enqueue(object : Callback<FlickrResponse> {
            override fun onResponse(
                call: Call<FlickrResponse>,
                response: Response<FlickrResponse>
            ) {

                val flickrResponse = response.body()
                val photoResponse = flickrResponse?.photos
                var galleryItems = photoResponse?.galleryItems ?: mutableListOf()
                galleryItems = galleryItems.filterNot {
                    it.url.isBlank()
                }

                responseLD.value = galleryItems

                Log.i(TAG, "photo list is received (${galleryItems.size} photos) ‼️")
            }

            override fun onFailure(call: Call<FlickrResponse>, t: Throwable) {
                Log.e(TAG, "failed to fetch photo", t)
            }
        })

        return responseLD
    }

    companion object {
        private const val TAG = "FlickrFetcher"
    }
}