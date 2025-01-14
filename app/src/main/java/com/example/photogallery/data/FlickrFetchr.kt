package com.example.photogallery.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.photogallery.api.FlickrApi
import com.example.photogallery.api.PhotoResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class FlickrFetcher @Inject constructor(private var flickrApi: FlickrApi) {

    fun fetchPhotos(page: Int): LiveData<List<GalleryItem>> {
        val responseLD = MutableLiveData<List<GalleryItem>>()
        val flickrRequest = flickrApi.fetchPhotos(page)

        flickrRequest.enqueue(object : Callback<PhotoResponse> {
            override fun onResponse(
                call: Call<PhotoResponse>,
                response: Response<PhotoResponse>
            ) {

                val photoResponse = response.body()
                var galleryItems = photoResponse?.galleryItems ?: mutableListOf()
                galleryItems = galleryItems.filterNot {
                    it.url.isBlank()
                }

                responseLD.value = galleryItems

                Log.i(TAG, "fetch is done ✅\n" +
                        "- photo list is received (${galleryItems.size} photos)")
            }

            override fun onFailure(call: Call<PhotoResponse>, t: Throwable) {
                Log.e(TAG, "failed to fetch photo", t)
            }
        })

        return responseLD
    }

    fun getPagingPhoto(): LiveData<PagingData<GalleryItem>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { FlickrPagingSource(flickrApi) }
        ).liveData
    }

    companion object {
        private const val TAG = "FlickrFetcher"
    }
}