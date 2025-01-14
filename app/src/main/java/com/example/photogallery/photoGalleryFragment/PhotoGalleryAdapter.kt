package com.example.photogallery.photoGalleryFragment

import android.util.Log
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.photogallery.data.GalleryItem

class PhotoGalleryAdapter: 
    PagingDataAdapter<GalleryItem, PhotoGalleryAdapter.PhotoGalleryViewHolder>(COMPARATOR) {

    override fun onBindViewHolder(holder: PhotoGalleryViewHolder, position: Int) {

        getItem(position)?.let {
            holder.bindTitle(it.title)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoGalleryViewHolder {
        val textView = TextView(parent.context)

        return PhotoGalleryViewHolder(textView)
    }

    inner class PhotoGalleryViewHolder(
        itemTextView: TextView
    ): RecyclerView.ViewHolder(itemTextView) {
        val bindTitle: (CharSequence) -> Unit = itemTextView::setText
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