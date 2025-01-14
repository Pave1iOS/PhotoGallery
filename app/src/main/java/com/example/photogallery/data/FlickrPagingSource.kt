package com.example.photogallery.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.photogallery.api.FlickrApi
import retrofit2.await
import javax.inject.Inject

class FlickrPagingSource @Inject constructor(
    private val flickrApi: FlickrApi
): PagingSource<Int, GalleryItem>() {

    override fun getRefreshKey(state: PagingState<Int, GalleryItem>): Int? {
        Log.d(TAG, "state: $state")
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GalleryItem> {

        val page = params.key ?: INITIAL_PAGE
        val response = flickrApi.fetchPhotos(page).await()
        val data = response.galleryItems.filterNot { it.url.isBlank() }

        Log.d(TAG, "page: $page")
        Log.d(TAG, "response: $response")
        Log.d(TAG, "data: $data")

        return try {
            LoadResult.Page(
                data = data,
                prevKey = page - 1,
                nextKey = page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    companion object {
        private const val INITIAL_PAGE = 1
        private const val TAG = "FlickrPagingSource"
    }
}