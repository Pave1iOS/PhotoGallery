package com.example.photogallery.photoGalleryFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.photogallery.App
import com.example.photogallery.R
import com.example.photogallery.data.FlickrFetcher
import com.example.photogallery.databinding.FragmentPhotoGalleryBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class PhotoGalleryFragment: Fragment(), MenuProvider {

    @Inject lateinit var flickrFetcher: FlickrFetcher
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[PhotoGalleryViewModel::class.java]
    }

    private lateinit var adapter: PhotoGalleryAdapter
    private lateinit var binding: FragmentPhotoGalleryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPhotoGalleryBinding.inflate(inflater, container, false)

        adapter = PhotoGalleryAdapter(inflater)

        binding.photoRecyclerView.adapter = adapter

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.instance.appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().addMenuProvider(this, viewLifecycleOwner)

        loadPhotos()
    }

    override fun onStart() {
        super.onStart()

        calculateDynamicColumnWithRecyclerView(binding.photoRecyclerView)

        loadingState()
        networkState()
        buttonNetworkReloadListener(binding.tryAgainButton)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.fragment_photo_gallery, menu)

        val menuSearch = menu.findItem(R.id.menu_item_search)
        val searchView = menuSearch.actionView as SearchView

        searchView.apply {
            queryTextListener(this)
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when(menuItem.itemId) {
            R.id.menu_item_clear -> {
                searchPhotos("popular") // изменить
                true
            }
            else -> super.onContextItemSelected(menuItem)
        }
    }

    private fun queryTextListener(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                Log.i(TAG, "🟢$MODULE_NAME called query search: $query")
                searchPhotos(query)

                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                Log.i(TAG, "🟢$MODULE_NAME called character search: $newText")
                // задержка перед поиском и посиск по последнему набранному
                lifecycleScope.launch {
                    delay(2000)

                    if (newText == LAST_QUERY_TEXT)

                        if (newText.isNotBlank()) {
                            searchPhotos(newText)
                        } else {
                            loadPhotos()
                        }
                }

                LAST_QUERY_TEXT = newText

                return true
            }
        })
    }

    private fun calculateDynamicColumnWithRecyclerView(view: RecyclerView) {

        // Ширина столбца
        val columnWidth = 350

        val listener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {

                // получение ширины и высоты представления
                val width = view.width
                val height = view.height

//                Log.d(TAG, "scene size:\nwidth = $width height = $height")

                // выбисление максимального значения (либо a либо b)
                val spanCount = maxOf(1, width / columnWidth)

                //утасновка этого значения в параметре количества столбцов
                view.layoutManager = GridLayoutManager(context, spanCount)

                // удаление слушателя что бы он не вызывался циклически при добавление элемента
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        }

        // добавляем слушателя к view
        view.viewTreeObserver.addOnGlobalLayoutListener(listener)
    }

    private fun loadAnimation(play: Boolean) {

        val visible = when(play) {
            true -> View.VISIBLE
            false -> View.GONE
        }

        binding.loadAnimation.visibility = visible

        when(play) {
            true -> {
                binding.photoRecyclerView.alpha = 0.5f

                Glide.with(requireActivity())
                    .asGif()
                    .load(R.drawable.full_scene_load_animation)
                    .into(binding.loadAnimation)
            }
            false -> {
                binding.photoRecyclerView.alpha = 1f

                Glide.with(requireActivity()).clear(binding.loadAnimation)
            }
        }
    }

    private fun showErrorNetworkMessage(status: Boolean) {

        val visible = when(status) {
            true -> View.GONE
            false -> View.VISIBLE
        }

        binding.errorDataNull.visibility = visible
    }

    private fun loadPhotos() {

        viewModel.queryCheck(viewLifecycleOwner) { lastQuery ->

            Log.i(TAG, "$MODULE_NAME last query = $lastQuery")

            if (lastQuery.isBlank()) {
                viewModel.loadPhotos().observe(viewLifecycleOwner) {
                    lifecycleScope.launch {
                        adapter.submitData(it)
                    }
                }
            } else {
                searchPhotos(lastQuery)
            }
        }
    }

    private fun searchPhotos(text: String) {
        viewModel.searchPhotos(text).observe(viewLifecycleOwner) {
            lifecycleScope.launch {
                adapter.submitData(it)
            }
        }
    }

    private fun loadingState() {
        viewModel.loadingState {
            if (isAdded)
                loadAnimation(it)
        }
    }

    private fun networkState() {
        viewModel.networkState {
            if (isAdded)
                showErrorNetworkMessage(it)
        }
    }

    private fun buttonNetworkReloadListener(button: Button) {
        button.setOnClickListener {
            loadPhotos()
        }
    }

    companion object {
        var LAST_QUERY_TEXT = ""
        private const val MODULE_NAME = "FRAGMENT ->"
        private const val TAG = "PhotoGalleryFragment"

        fun newInstance(): PhotoGalleryFragment {
            return PhotoGalleryFragment()
        }
    }
}
