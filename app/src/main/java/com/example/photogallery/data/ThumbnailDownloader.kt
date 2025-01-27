package com.example.photogallery.data

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

class ThumbnailDownloader<in T> @Inject constructor(
    private val flickrFetcher: FlickrFetcher,
    private val responseHandler: Handler,
    private val onThumbnailDownloader: (T, Bitmap) -> Unit
): HandlerThread(TAG) {

    private var hasQuit = false
    private lateinit var requestHandler: Handler
    private val requestMap = ConcurrentHashMap<T, String>()

    @Suppress("UNCHECKED_CAST")
    @SuppressLint("HandlerLeak")
    override fun onLooperPrepared() {
        requestHandler = object : Handler(looper) { // delete looper
            override fun handleMessage(msg: Message) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    val target = msg.obj as T
                    Log.i(TAG, "Got a request for URL: ${requestMap[target]}")
                    handleRequest(target)
                }
            }
        }
        Log.i(TAG, "id looper: $looper")
    }

    override fun quit(): Boolean {
        hasQuit = true
        return super.quit()
    }

    private fun handleRequest(target: T) {
        val url = requestMap[target] ?: return
        val bitmap = flickrFetcher.fetchPhoto(url) ?: return

        responseHandler.post(Runnable {
            if (requestMap[target] != url || hasQuit) {
                return@Runnable
            }

            requestMap.remove(target)
            onThumbnailDownloader(target, bitmap)
        })

    }

    fun queueThumbnail(target: T, url: String) {
        requestMap[target] = url
        requestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget()

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
        private const val MESSAGE_DOWNLOAD = 0
        private const val TAG = "ThumbnailDownloader"
    }
}