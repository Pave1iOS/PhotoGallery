package com.example.photogallery.api

import okhttp3.Interceptor
import okhttp3.Response

class FlickrInterceptor: Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val origRequest = chain.request()

        val newURL = origRequest.url().newBuilder()
            .addQueryParameter("api_key", API_KEY)
            .addQueryParameter("format", FORMAT_JSON)
            .addQueryParameter("nojsoncallback", NO_JSON_CALLBACK_CODE)
            .addQueryParameter("extras", EXTRAS_URLS)
            .build()

        val newRequest = origRequest.newBuilder()
            .url(newURL)
            .build()

        return chain.proceed(newRequest)
    }

    companion object {
        private const val API_KEY = "1c4e843d45bd2cb9d2173f07b05f9883"
        private const val FORMAT_JSON = "json"
        private const val NO_JSON_CALLBACK_CODE = "1"
        private const val EXTRAS_URLS = "url_s"
    }
}