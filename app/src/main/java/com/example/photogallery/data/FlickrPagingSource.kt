package com.example.photogallery.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import javax.inject.Inject

class FlickrPagingSource @Inject constructor(
    private val flickrFetcher: FlickrFetcher
): PagingSource<Int, GalleryItem>() {

    override fun getRefreshKey(state: PagingState<Int, GalleryItem>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GalleryItem> {

        val page = params.key ?: INITIAL_PAGE
        val data = flickrFetcher.fetchPhotos(page)

        Log.d(TAG, "page: $page")
        Log.d(TAG, " load: ${data.size} photos")

        return try {
            LoadResult.Page(
                data = data,
                prevKey = if (page == INITIAL_PAGE) null else page - 1,
                nextKey = if (data.isEmpty()) null else page + 1
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