package com.example.photogallery.api

import android.util.Log
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonSyntaxException
import java.lang.reflect.Type

class PhotoDeserializer: JsonDeserializer<PhotoResponse> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): PhotoResponse {

        if (!json.isJsonObject)
            throw JsonSyntaxException("Expected JSON Object but found: ${json.javaClass.simpleName}")

        val data = json.asJsonObject
        Log.d(TAG, "data = $data")

        if (!data.isJsonObject)
            throw JsonSyntaxException("Expected JSON Object 'photos'")

        val photosObject = data.getAsJsonObject("photos")
        Log.d(TAG, "photosObject = $photosObject")

        if (!photosObject.isJsonArray)
            throw JsonSyntaxException("Expected JSON Array for 'photo' inside 'photos'")

        val photosArray = photosObject.getAsJsonArray("photo")
        val result: PhotoResponse? = context?.deserialize(photosArray, PhotoResponse::class.java)

        Log.d(TAG, "photosArray = $photosArray")

        return result ?: PhotoResponse()
    }

    companion object {
        private const val TAG = "PhotoDeserializer"
    }
}