package com.example.photogallery.data

import android.util.Log
import com.example.photogallery.api.FlickrApi
import com.example.photogallery.api.FlickrResponse
import com.example.photogallery.api.GalleryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FlickrFetcher @Inject constructor(private val flickrApi: FlickrApi) {

    var isLoadingState = MutableStateFlow(true)

    suspend fun fetchPhotos(page: Int): List<GalleryItem> {
        return fetchPhotoMetadata { flickrApi.fetchPhotos(page) }
    }

    suspend fun searchPhotos(text: String): List<GalleryItem> {
        return fetchPhotoMetadata { flickrApi.searchPhotos(text) }
    }

    private suspend fun fetchPhotoMetadata(request: () -> Call<FlickrResponse>): List<GalleryItem> {
        return withContext(Dispatchers.IO) {

            isLoadingState.value = true
            Log.i(TAG, "🟡load data is started")

            try {
                val response = executeRequest(request)
                val galleryItems = response.galleryItems.filterNot { it.url.isBlank() }
                Log.i(
                    TAG, "🟢load data is finish\n" +
                            "- photo list is received (${galleryItems.size} photos)"
                )
                galleryItems
            } catch (e: Exception) {
                Log.e(TAG, "🔴Filed to fetch photo", e)
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
                        continuation.resumeWithException(IllegalStateException(
                            "🔴response body is null"
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
        private const val TAG = "FlickrFetcher"
    }
}
