package com.example.photogallery.data

import android.util.Log
import com.example.photogallery.api.FlickrApi
import com.example.photogallery.api.GalleryItem
import com.example.photogallery.api.FlickrResponse
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

    private suspend fun fetchPhotoMetadata(flickrRequest: Call<FlickrResponse>): List<GalleryItem> {
        return suspendCancellableCoroutine { continuation ->

            flickrRequest.enqueue(object : Callback<FlickrResponse> {
                override fun onResponse(
                    call: Call<FlickrResponse>,
                    response: Response<FlickrResponse>
                ) {
                    val photoResponse = response.body()
                    val galleryItems = photoResponse?.galleryItems?.filterNot {
                        it.url.isBlank()
                    } ?: emptyList()

                    continuation.resume(galleryItems)

                    Log.i(TAG, "fetch is done ✅\n" +
                            "- photo list is received (${galleryItems.size} photos)")
                }

                override fun onFailure(call: Call<FlickrResponse>, t: Throwable) {
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
