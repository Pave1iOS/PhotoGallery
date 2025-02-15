package com.example.photogallery.photoGalleryFragment

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSourceFactory
import androidx.paging.cachedIn
import com.example.photogallery.api.GalleryItem
import com.example.photogallery.data.FlickrDataStore
import com.example.photogallery.data.FlickrFetcher
import com.example.photogallery.data.FlickrPagingSource
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @property _savedFindPhoto для сохранения прогресса RecyclerView
 * @property progress для сохранения прогресса RecyclerView
 * @property _storedQuery для сохранения запроса при перезапуске приложения
 */

class PhotoGalleryViewModel @Inject constructor(
    private val flickrFetcher: FlickrFetcher,
    private val app: Application
): AndroidViewModel(app) {

    var photos = MutableLiveData<PagingData<GalleryItem>>()

    private var _savedLoadPhoto: Flow<PagingData<GalleryItem>>? = null
    private var _savedFindPhoto: Flow<PagingData<GalleryItem>>? = null

    private var _storedQuery = MutableStateFlow<String?>(null)

    fun initializeData() {

        viewModelScope.launch {

            loadStoredQuery().join()
            Log.d(TAG, "$MODULE_NAME stored query after initialize: ${_storedQuery.value}")

            if (_storedQuery.value.isNullOrBlank()) {
                observeLoad()
            } else {
                _storedQuery.value?.let { storedQuery ->
                    observeFind(storedQuery)
                }
            }
        }
    }

    fun getPhotoByQuery(query: String) {
        photos.value = PagingData.empty()
        observeFind(query)
    }

    fun getAllPhoto() {
        observeLoad()
    }

    fun loadingState(state: (Boolean) -> Unit) {
        observeState(flickrFetcher.isLoadingState, state)
    }

    fun networkState(state: (Boolean) -> Unit) {
        observeState(flickrFetcher.isNetworkState, state)
    }

    fun clearStoredQuery() {
        saveStorageQuery("")

        Log.d(TAG, "clear stored query")
    }

    private fun loadPhotoFlow(): Flow<PagingData<GalleryItem>> {

        Log.i(TAG, "🟢$MODULE_NAME load photo init")

        return _savedLoadPhoto
            ?: fetchPagingData { page ->
                Log.d(TAG, "current page: $page")

                flickrFetcher.fetchAllPhoto(page)
            }
    }

    private fun findPhotoFlow(text: String): Flow<PagingData<GalleryItem>> {

        saveStorageQuery(text)

        Log.i(TAG, "$MODULE_NAME search photo init")

        return _savedFindPhoto
            ?: fetchPagingData { page ->
                Log.d(TAG, "current page: $page")

                flickrFetcher.fetchFindPhoto(text, page)
            }
    }

    private fun observeLoad() {

        _savedLoadPhoto = loadPhotoFlow()

        viewModelScope.launch {
            loadPhotoFlow().collect {
                photos.value = it
            }
        }
    }

    private fun observeFind(text: String) {

        _savedFindPhoto = findPhotoFlow(text)

        viewModelScope.launch {
            findPhotoFlow(text).collect {
                photos.value = it
            }
        }
    }

    private fun observeState(flow: MutableStateFlow<Boolean>, state: (Boolean) -> Unit) {
        viewModelScope.launch {
            flow.collect {
                if (it) state(true) else state(false)
            }
        }
    }

    private fun saveStorageQuery(text: String) {
        viewModelScope.launch {
            FlickrDataStore.setStoredQuery(app, text)
        }
    }

    private fun loadStoredQuery(): Job {
        return viewModelScope.launch {
            FlickrDataStore.getStoredQuery(app)
                .take(1)
                .collect { query ->
                _storedQuery.value = query

//                Log.i(TAG, "$MODULE_NAME load stored query: $query")
            }
        }
    }

    private fun fetchPagingData(
        handler: suspend (Int) -> List<GalleryItem>
    ): Flow<PagingData<GalleryItem>> {

        val config = PagingConfig(PAGE_SIZE)

        val factory = PagingSourceFactory {
            FlickrPagingSource { page ->
                handler(page)
            }
        }

        return Pager(config, pagingSourceFactory = factory)
            .flow
            .cachedIn(viewModelScope)
    }

    companion object {
        private const val PAGE_SIZE = 100
        private const val MODULE_NAME = "VIEW MODEL ->"
        private const val TAG = "PhotoGalleryViewModel"
    }
}