package com.example.photogallery.data

import android.os.HandlerThread
import android.util.Log

class ThumbnailDownloader<in T>: HandlerThread(TAG) {

    private var hasQuit = false

    override fun quit(): Boolean {
        hasQuit = true
        return super.quit()
    }

    fun queueThumbnail(url: String) {
        Log.i(TAG, "⬇️Download picture: $url")
    }

    fun startThread() {
        Log.i(TAG, "✅Starting background thread")

        start()
        looper
    }

    fun stopThread() {
        Log.i(TAG, "⛔️Destroying background thread")

        quit()
    }

    companion object {
        private const val TAG = "ThumbnailDownloader"
    }
}