package com.example.photogallery.api

import com.example.photogallery.data.GalleryItem
import com.google.gson.annotations.SerializedName

class PhotoResponse {
    @SerializedName("photo")
    lateinit var galleryItems: List<GalleryItem>
}