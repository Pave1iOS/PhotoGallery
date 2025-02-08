package com.example.photogallery.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrApi {
    @GET("services/rest/?method=flickr.interestingness.getList")
    fun fetchAllPhoto(@Query("page") page: Int): Call<FlickrResponse>

    @GET("services/rest/?method=flickr.photos.search")
    fun fetchFindPhoto(
        @Query("text") text: String,
        @Query("page") page: Int
    ): Call<FlickrResponse>
}