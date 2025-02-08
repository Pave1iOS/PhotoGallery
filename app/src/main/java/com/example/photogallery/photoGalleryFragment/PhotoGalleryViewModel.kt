package com.example.photogallery.photoGalleryFragment

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSourceFactory
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.example.photogallery.api.GalleryItem
import com.example.photogallery.data.FlickrDataStore
import com.example.photogallery.data.FlickrFetcher
import com.example.photogallery.data.FlickrPagingSource
import com.example.photogallery.photoGalleryFragment.PhotoGalleryFragment.Companion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @property _currentPhotos для сохранения прогресса RecyclerView
 * @property progress для сохранения прогресса RecyclerView
 * @property _storedQuery для сохранения запроса при перезапуске приложения
 */

class PhotoGalleryViewModel @Inject constructor(
    private val flickrFetcher: FlickrFetcher,
    private val app: Application
): AndroidViewModel(app) {

    private var _currentPhotos: LiveData<PagingData<GalleryItem>>? = null
    private var progress = MutableLiveData(0)

    private var _storedQuery = MutableStateFlow<String?>(null)

    var loadingData = MutableLiveData<PagingData<GalleryItem>>()

    fun initializeData(owner: LifecycleOwner) {
        viewModelScope.launch {

            loadStoredQuery().join()
            Log.d(TAG, "$MODULE_NAME stored query after initialize: ${_storedQuery.value}")

            if (_storedQuery.value.isNullOrBlank()) {
                observe(loadPhotos(), owner)
            } else {
                _storedQuery.value?.let { storedQuery ->
                    observe(searchPhotos(storedQuery), owner)
                }
            }
        }
    }

    fun searchByPhoto(text: String, owner: LifecycleOwner) {
        loadingData.value = PagingData.empty()
        observe(searchPhotos(text), owner)
    }

    fun loadingState(state: (Boolean) -> Unit) {
        observeState(flickrFetcher.isLoadingState, state)
    }

    fun networkState(state: (Boolean) -> Unit) {
        observeState(flickrFetcher.isNetworkState, state)
    }

    fun clearStoredQuery() {
        _storedQuery.value = ""
    }

    private fun observe(method: LiveData<PagingData<GalleryItem>>, owner: LifecycleOwner) {
        method.observe(owner) {
            loadingData.value = it
        }
    }

    private fun loadPhotos(): LiveData<PagingData<GalleryItem>> {

        if (_currentPhotos == null || progress.value == 0) {
            _currentPhotos = fetchPagingData { page ->
                progress.value = page
                flickrFetcher.fetchPhotos(page)
            }
        }
        Log.d(TAG, "load photo checked")
        Log.i(TAG, "🟢$MODULE_NAME load photo progress = ${progress.value}")

        return _currentPhotos
            ?: fetchPagingData {  page ->
                flickrFetcher.fetchPhotos(page)
            }
    }

    private fun searchPhotos(text: String): LiveData<PagingData<GalleryItem>> {

        viewModelScope.launch {
            FlickrDataStore.setStoredQuery(app, text)
        }

        progress.value = 0

        Log.i(TAG, "$MODULE_NAME search photo progress = ${progress.value}")
        Log.d(TAG, "search photo checked")

        return fetchPagingData {
            flickrFetcher.searchPhotos(text)
        }
    }

    private fun observeState(flow: MutableStateFlow<Boolean>, state: (Boolean) -> Unit) {
        viewModelScope.launch {
            flow.collect {
                if (it) state(true) else state(false)
            }
        }
    }

    private fun loadStoredQuery(): Job {
        return viewModelScope.launch {
            FlickrDataStore.getStoredQuery(app)
                .take(1)
                .collect { query ->
                _storedQuery.value = query

                Log.i(TAG, "$MODULE_NAME load stored query: $query")
            }
        }
    }

    private fun fetchPagingData(
        handler: suspend (Int) -> List<GalleryItem>
    ): LiveData<PagingData<GalleryItem>> {

        val config = PagingConfig(PAGE_SIZE)

        val factory = PagingSourceFactory {
            FlickrPagingSource { page ->
                handler(page)
            }
        }

        return Pager(config, pagingSourceFactory = factory)
            .liveData
            .cachedIn(viewModelScope)
    }

    companion object {
        private const val PAGE_SIZE = 100
        private const val MODULE_NAME = "VIEW MODEL ->"
        private const val TAG = "PhotoGalleryViewModel"
    }
}