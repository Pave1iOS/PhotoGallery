package com.example.photogallery.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.photogallery.api.FlickrApi
import com.example.photogallery.api.PhotoResponse
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import kotlin.coroutines.resume

class FlickrFetcher @Inject constructor(private val flickrApi: FlickrApi) {

    suspend fun fetchPhotos(page: Int): List<GalleryItem> {
        return fetchPhotoMetadata(flickrApi.fetchPhotos(page))
    }

    suspend fun searchPhotos(text: String): List<GalleryItem> {
        return fetchPhotoMetadata(flickrApi.searchPhotos(text))
    }

    private suspend fun fetchPhotoMetadata(flickrRequest: Call<PhotoResponse>): List<GalleryItem> {
        return suspendCancellableCoroutine { continuation ->

            flickrRequest.enqueue(object : Callback<PhotoResponse> {
                override fun onResponse(
                    call: Call<PhotoResponse>,
                    response: Response<PhotoResponse>
                ) {
                    val photoResponse = response.body()
                    val galleryItems = photoResponse?.galleryItems?.filterNot {
                        it.url.isBlank()
                    } ?: emptyList()

                    continuation.resume(galleryItems)

                    Log.i(TAG, "fetch is done ✅\n" +
                            "- photo list is received (${galleryItems.size} photos)")
                }

                override fun onFailure(call: Call<PhotoResponse>, t: Throwable) {
                    Log.e(TAG, "failed to fetch photo", t)
                    continuation.cancel(t)
                }
            })

            continuation.invokeOnCancellation {
                throw RuntimeException("coroutine canceled ‼️", it)
            }
        }
    }

    companion object {
        private const val TAG = "FlickrFetcher"
    }
}
