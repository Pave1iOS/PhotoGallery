package com.example.photogallery.photoGalleryFragment

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.photogallery.R
import com.example.photogallery.data.GalleryItem

class PhotoGalleryAdapter(
    private val layoutInflater: LayoutInflater,
    private val context: Context,
    private val viewModel: PhotoGalleryViewModel
): PagingDataAdapter<GalleryItem, PhotoGalleryAdapter.PhotoGalleryViewHolder>(COMPARATOR) {

    override fun onBindViewHolder(holder: PhotoGalleryViewHolder, position: Int) {

        val placeholder = ContextCompat.getDrawable(
            context,
            R.drawable.image_placeholder
        ) ?: ColorDrawable()

        holder.bindDrawable(placeholder)

        getItem(position)?.let {
            viewModel.downloadPicture(holder, it.url)
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
        itemImageView: ImageView
    ): RecyclerView.ViewHolder(itemImageView) {
        val bindDrawable: (Drawable) -> Unit = itemImageView::setImageDrawable
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