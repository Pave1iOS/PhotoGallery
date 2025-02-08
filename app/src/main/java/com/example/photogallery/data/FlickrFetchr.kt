package com.example.photogallery.data

import android.util.Log
import com.example.photogallery.api.FlickrApi
import com.example.photogallery.api.FlickrResponse
import com.example.photogallery.api.GalleryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FlickrFetcher @Inject constructor(private val flickrApi: FlickrApi) {

    var isNetworkState = MutableStateFlow(true)
    var isLoadingState = MutableStateFlow(true)

    suspend fun fetchPhotos(page: Int): List<GalleryItem> {
        return fetchPhotoMetadata { flickrApi.fetchPhotos(page) }
    }

    suspend fun searchPhotos(text: String, page: Int): List<GalleryItem> {
        return fetchPhotoMetadata { flickrApi.searchPhotos(text, page) }
    }

    private suspend fun fetchPhotoMetadata(request: () -> Call<FlickrResponse>): List<GalleryItem> {
        return withContext(Dispatchers.IO) {

            isNetworkState.value = true
            isLoadingState.value = true

            Log.i(TAG, "🟡$MODULE_MANE load data is starting")

            try {
                val response = executeRequest(request)
                val uniqueURL = mutableSetOf<String>()

                val galleryItems = response.galleryItems.filterNot {
                    it.url.isBlank() || !uniqueURL.add(it.url)
                }

                Log.i(
                    TAG, "🟢$MODULE_MANE load data is finish\n" +
                            "・${galleryItems.size} images is loaded"
                )
                galleryItems
            } catch (e: Exception) {
                Log.e(TAG, "🔴$MODULE_MANE filed to fetch photo", e)
                isNetworkState.value = false
                throw e
            } finally {
                isLoadingState.value = false
            }
        }
    }

    private suspend fun executeRequest(request: () -> Call<FlickrResponse>): FlickrResponse {
        return suspendCoroutine { continuation ->
            request().enqueue(object : Callback<FlickrResponse> {
                override fun onResponse(p0: Call<FlickrResponse>, p1: Response<FlickrResponse>) {
                    val body = p1.body()

                    if (body != null) {
                        continuation.resume(body)
                    } else {
                        isNetworkState.value = false
                        continuation.resumeWithException(IllegalStateException(
                            "🔴$MODULE_MANE response body is null"
                        ))
                    }
                }

                override fun onFailure(p0: Call<FlickrResponse>, p1: Throwable) {
                    continuation.resumeWithException(p1)
                }
            })
        }
    }

    companion object {
        private const val MODULE_MANE = "NETWORK ->"
        private const val TAG = "FlickrFetcher"
    }
}
