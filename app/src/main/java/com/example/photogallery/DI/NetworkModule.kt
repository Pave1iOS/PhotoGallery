package com.example.photogallery.DI

import com.example.photogallery.api.FlickrApi
import com.example.photogallery.api.PhotoInterceptor
import com.example.photogallery.api.PhotoResponse
import com.example.photogallery.data.FlickrFetcher
import com.example.photogallery.data.PagerFetcher
import com.example.photogallery.data.PhotoDeserializer
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
            .registerTypeAdapter(PhotoResponse::class.java, PhotoDeserializer())
            .create()

        val gsonFactory = GsonConverterFactory.create(gson)

        val client = OkHttpClient.Builder()
            .addInterceptor(PhotoInterceptor())
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

    @Provides
    fun getPagerFetcher(flickrFetcher: FlickrFetcher): PagerFetcher {
        return PagerFetcher(flickrFetcher)
    }
}