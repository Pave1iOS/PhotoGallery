package com.example.photogallery.api

import android.util.Log
import com.example.photogallery.data.GalleryItem
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class PhotoDeserializer : JsonDeserializer<PhotoResponse> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): PhotoResponse {

        if (!json.isJsonObject) {
            throw JsonSyntaxException("json: JSON Object but found: ${json.javaClass.simpleName}")
        }

        val photosObject = json.asJsonObject
        Log.d(TAG, "photosObject = $photosObject")

        if (!photosObject.has("photo") || !photosObject["photo"].isJsonArray){
            throw JsonSyntaxException("data: JSON Object 'photos' not found")
        }

        val photosArray = photosObject.getAsJsonArray("photo")

        Log.d(TAG, "photosArray = $photosArray")

        val galleryItems: List<GalleryItem> = context?.deserialize(
            photosArray,
            object: TypeToken<List<GalleryItem>>(){}.type
        ) ?: listOf()

        Log.d(TAG, "galleryItems: $galleryItems")

        return PhotoResponse().apply {
            this.galleryItems = galleryItems
        }
    }

    companion object {
        private const val TAG = "PhotoDeserializer"
    }
}