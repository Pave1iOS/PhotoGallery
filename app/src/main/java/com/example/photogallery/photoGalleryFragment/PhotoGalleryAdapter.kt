package com.example.photogallery.photoGalleryFragment

import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.photogallery.GalleryItem

class PhotoGalleryAdapter(
    private val galleryItems: List<GalleryItem>
): RecyclerView.Adapter<PhotoGalleryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoGalleryViewHolder {
        val textView = TextView(parent.context)
        return PhotoGalleryViewHolder(textView)
    }

    override fun getItemCount(): Int = galleryItems.size

    override fun onBindViewHolder(holder: PhotoGalleryViewHolder, position: Int) {
        val galleryItem = galleryItems[position]

        holder.bindTitle(galleryItem.title)
    }
}