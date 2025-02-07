package com.example.photogallery.photoGalleryFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.photogallery.R
import com.example.photogallery.api.GalleryItem
import com.squareup.picasso.Picasso

class PhotoGalleryAdapter(
    private val layoutInflater: LayoutInflater
): PagingDataAdapter<GalleryItem, PhotoGalleryAdapter.PhotoGalleryViewHolder>(COMPARATOR) {

    override fun onBindViewHolder(holder: PhotoGalleryViewHolder, position: Int) {

        getItem(position)?.let {
            holder.bind(it)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoGalleryViewHolder {

        val view = layoutInflater.inflate(
            R.layout.list_item_gallery,
            parent,
            false) as ImageView

        return PhotoGalleryViewHolder(view)
    }

    inner class PhotoGalleryViewHolder(
        private val itemImageView: ImageView
    ): RecyclerView.ViewHolder(itemImageView) {

        fun bind(item: GalleryItem) {
            Picasso.get()
                .load(item.url)
                .placeholder(R.drawable.image_load_animation)
                .error(R.drawable.error_load_image)
                .into(itemImageView)
        }
    }

    companion object {
        private const val TAG = "PhotoGalleryAdapter"

        private val COMPARATOR = object : DiffUtil.ItemCallback<GalleryItem>() {
            override fun areItemsTheSame(oldItem: GalleryItem, newItem: GalleryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: GalleryItem, newItem: GalleryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}