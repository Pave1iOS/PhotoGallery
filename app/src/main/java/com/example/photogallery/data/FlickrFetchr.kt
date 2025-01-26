package com.example.photogallery.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.photogallery.api.FlickrApi
import com.example.photogallery.api.PhotoResponse
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Url
import javax.inject.Inject
import kotlin.coroutines.resume

class FlickrFetcher @Inject constructor(private val flickrApi: FlickrApi) {

    suspend fun fetchPhotos(page: Int): List<GalleryItem> {
        return suspendCancellableCoroutine { continuation ->

            val flickrRequest = flickrApi.fetchPhotos(page)

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

    fun getPagingPhoto(): LiveData<PagingData<GalleryItem>> {
        return Pager(
            config = PagingConfig(pageSize = 100),
            pagingSourceFactory = { FlickrPagingSource(this) }
        ).liveData
    }

    @WorkerThread
    fun fetchPhoto(url: String): Bitmap? {
        val response = flickrApi.fetchURLBytes(url).execute()
        val bitmap = response.body()?.byteStream()?.use(BitmapFactory::decodeStream)
        return bitmap
    }

    companion object {
        private const val TAG = "FlickrFetcher"
    }
}
