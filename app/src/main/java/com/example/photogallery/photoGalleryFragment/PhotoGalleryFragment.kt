package com.example.photogallery.photoGalleryFragment

import android.icu.text.RelativeDateTimeFormatter.Direction
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.bumptech.glide.Glide
import com.example.photogallery.App
import com.example.photogallery.R
import com.example.photogallery.data.FlickrFetcher
import javax.inject.Inject

class PhotoGalleryFragment: Fragment(), MenuProvider {

    @Inject lateinit var flickrFetcher: FlickrFetcher
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

        adapter = PhotoGalleryAdapter(inflater)

        photoRecyclerView.adapter = adapter

        recyclerViewScrollListener(photoRecyclerView)

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.instance.appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().addMenuProvider(this, viewLifecycleOwner)

        viewModel.galleryItems.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.loadPhotos()

        viewModel.loadingState {
            playLoadAnimation(it)
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

                    viewModel.loadPhotos(query)

                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    Log.d(TAG, "search query: $newText")

                    if (newText.isNotBlank()) {
                        viewModel.searchPhotos(newText)
                    } else {
                        viewModel.loadPhotos()
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

    private fun playLoadAnimation(playAnimation: Boolean) {

        val visible = when(playAnimation) {
            true -> View.VISIBLE
            false -> View.GONE
        }

        gifImageView.visibility = visible

        when(playAnimation) {
            true -> {
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

    private fun recyclerViewScrollListener(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!recyclerView.canScrollVertically(1)) {
                    viewModel.loadPhotos()
                }
            }
        })
    }

    companion object {
        private const val TAG = "PhotoGalleryFragment"
        fun newInstance(): PhotoGalleryFragment {

            return PhotoGalleryFragment()

        }
    }
}
