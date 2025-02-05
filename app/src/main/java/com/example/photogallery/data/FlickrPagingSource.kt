package com.example.photogallery.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.photogallery.api.GalleryItem

class FlickrPagingSource(
    private val loader: suspend (page: Int) -> List<GalleryItem>
): PagingSource<Int, GalleryItem>() {

    override fun getRefreshKey(state: PagingState<Int, GalleryItem>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GalleryItem> {
        val page = params.key ?: STARTING_PAGE
        val data = loader(page)
        val prevKey = if (page == STARTING_PAGE) null else page - 1
        val nextKey = if (data.isEmpty()) null else page + 1

        Log.i(TAG, "🟢$MODULE_NAME $page page is loaded")

        return try {
            LoadResult.Page(
                data,
                prevKey,
                nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    companion object {
        private const val STARTING_PAGE = 1
        private const val TAG = "FlickrPagingSource"
        private const val MODULE_NAME = "PAGING SOURCE ->"
    }
}