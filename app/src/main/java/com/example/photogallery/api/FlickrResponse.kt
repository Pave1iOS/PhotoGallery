package com.example.photogallery.api

import com.google.gson.annotations.SerializedName

class FlickrResponse {
    @SerializedName("photo")
    lateinit var galleryItems: List<GalleryItem>
}

data class GalleryItem(
    var title: String = "",
    var id: String = "",

    @SerializedName("url_s")
    var url: String = ""
)