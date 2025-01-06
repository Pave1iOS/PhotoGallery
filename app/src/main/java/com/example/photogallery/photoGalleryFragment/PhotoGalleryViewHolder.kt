package com.example.photogallery.photoGalleryFragment

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PhotoGalleryViewHolder(itemTextView: TextView):RecyclerView.ViewHolder(itemTextView) {
    val bindTitle: (CharSequence) -> Unit = itemTextView::setText
}
