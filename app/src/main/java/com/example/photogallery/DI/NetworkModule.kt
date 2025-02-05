package com.example.photogallery.DI

import com.example.photogallery.api.FlickrApi
import com.example.photogallery.api.FlickrDeserializer
import com.example.photogallery.api.FlickrInterceptor
import com.example.photogallery.api.FlickrResponse
import com.example.photogallery.data.FlickrFetcher
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
class NetworkModule {

    @Provides
    fun getRetrofit(): Retrofit {

        val gson = GsonBuilder()
            .registerTypeAdapter(FlickrResponse::class.java, FlickrDeserializer())
            .create()

        val gsonFactory = GsonConverterFactory.create(gson)

        val client = OkHttpClient.Builder()
            .addInterceptor(FlickrInterceptor())
            .build()

        return Retrofit.Builder()
            .baseUrl("https://api.flickr.com/")
            .addConverterFactory(gsonFactory)
            .client(client)
            .build()
    }

    @Provides
    fun getFlickrApi(retrofit: Retrofit): FlickrApi {
        return retrofit.create(FlickrApi::class.java)
    }

    @Provides
    fun getFlickrFetcher(flickrApi: FlickrApi): FlickrFetcher {
        return FlickrFetcher(flickrApi)
    }
}