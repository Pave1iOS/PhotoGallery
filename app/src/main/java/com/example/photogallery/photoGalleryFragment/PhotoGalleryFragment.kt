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
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.photogallery.App
import com.example.photogallery.R
import com.example.photogallery.data.FlickrFetcher
import com.example.photogallery.data.PagerFetcher
import kotlinx.coroutines.launch
import javax.inject.Inject

class PhotoGalleryFragment: Fragment(), MenuProvider {

    @Inject lateinit var flickrFetcher: FlickrFetcher
    @Inject lateinit var pagerFetcher: PagerFetcher
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[PhotoGalleryViewModel::class.java]
    }

    private lateinit var photoRecyclerView: RecyclerView
    private lateinit var adapter: PhotoGalleryAdapter
    private lateinit var gifImageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_photo_gallery, container,false)
        gifImageView = view.findViewById(R.id.load_animation)

        photoRecyclerView = view.findViewById(R.id.photo_recycler_view)
        adapter = PhotoGalleryAdapter(layoutInflater)

        photoRecyclerView.adapter = adapter

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.instance.appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listPhoto()

        requireActivity().addMenuProvider(this, viewLifecycleOwner)

        lifecycleScope.launch {
            flickrFetcher.isLoading.collect {



            }
        }

    }

    override fun onStart() {
        super.onStart()

        calculateDynamicColumnWithRecyclerView(photoRecyclerView)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.fragment_photo_gallery, menu)

        val menuSearch = menu.findItem(R.id.menu_item_search)
        val searchView = menuSearch.actionView as SearchView

        searchView.apply {

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    Log.i(TAG, "Search text: $query")


                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    Log.i(TAG, "character is pressed: $newText")

                    Log.e(TAG, "flickr value gone tap -> ${flickrFetcher.isLoading}")

                    if (newText.isNotEmpty()) {
                        searchPhoto(newText)
                    } else {
                        listPhoto()
                    }

                    return true
                }
            })
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return true
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

    private fun searchPhoto(text: String) {
        viewModel.searchPhoto(text).observe(viewLifecycleOwner) {
            lifecycleScope.launch {
                adapter.submitData(it)
            }
        }
    }

    private fun listPhoto() {
        viewModel.getPhoto().observe(viewLifecycleOwner) {
            lifecycleScope.launch {
                adapter.submitData(it)
            }
        }
    }

    private fun cleanList() {
        lifecycleScope.launch {
            adapter.submitData(PagingData.empty())
        }
    }

    private fun playLoadAnimation(playAnimation: Boolean) {

        val visible = when(playAnimation) {
            true -> View.VISIBLE
            false -> View.GONE
        }

        gifImageView.visibility = visible

        when(playAnimation) {
            true -> {
                cleanList()

                Glide.with(this)
                    .asGif()
                    .load(R.drawable.full_scene_load_animation)
                    .into(gifImageView)
            }
            false -> {
                Glide.with(this).clear(gifImageView)
            }
        }
    }

    companion object {
        private const val TAG = "PhotoGalleryFragment"
        fun newInstance(): PhotoGalleryFragment {

            return PhotoGalleryFragment()

        }
    }
}
