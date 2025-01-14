package com.example.photogallery.api

import androidx.paging.PagingData
import com.example.photogallery.data.GalleryItem
import com.google.gson.annotations.SerializedName

class PhotoResponse {
    @SerializedName("photo")
    lateinit var galleryItems: List<GalleryItem>
}