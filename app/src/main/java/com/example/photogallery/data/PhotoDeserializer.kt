package com.example.photogallery.data

import android.util.Log
import com.example.photogallery.api.PhotoResponse
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

        val jsonObject = json.asJsonObject
        Log.d(TAG, "jsonObject = $jsonObject")

        if (!jsonObject.has("photos") || !jsonObject["photos"].isJsonObject){
            throw JsonSyntaxException("jsonObject: JSON Object 'photos' not found")
        }

        val photosObject = jsonObject.getAsJsonObject("photos")
        Log.d(TAG, "photosObject = $photosObject")

        if (!photosObject.has("photo") || !photosObject["photo"].isJsonArray){
            throw JsonSyntaxException("data: JSON Object 'photos' not found")
        }

        val photoArray = photosObject.getAsJsonArray("photo")

        Log.d(TAG, "photoArray = $photoArray")

        val galleryItems: List<GalleryItem> = context?.deserialize(
            photoArray,
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
