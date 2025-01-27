package com.example.photogallery.data

import android.annotation.SuppressLint
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

class ThumbnailDownloader<in T> @Inject constructor(
    private val flickrFetcher: FlickrFetcher
): HandlerThread(TAG) {

    private var hasQuit = false
    private lateinit var requestHandler: Handler
    private val requestMap = ConcurrentHashMap<T, String>()

    // вызывается до того, как Looper впервые проверит очередь
    @Suppress("UNCHECKED_CAST")
    @SuppressLint("HandlerLeak")
    override fun onLooperPrepared() {
        requestHandler = object : Handler(looper) { // delete looper
            //handleMessage вызывается для обработки каждого приходящего сообщения
            //msg — это сообщение, отправленное в Handler (отправленное в методе queueThumbnail).
            //msg.what проверяется на соответствие MESSAGE_DOWNLOAD.
            //msg.obj (который равен переданному target при создании сообщения) приводится к
            //типу T и затем используется в handleRequest.
            override fun handleMessage(msg: Message) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    val target = msg.obj as T
                    Log.i(TAG, "Got a request for URL: ${requestMap[target]}")
                    handleRequest(target)
                }
            }
        }
    }

    override fun quit(): Boolean {
        hasQuit = true
        return super.quit()
    }

    private fun handleRequest(target: T) {
        val url = requestMap[target] ?: return
        val bitmap = flickrFetcher.fetchPhoto(url) ?: return
    }

    fun queueThumbnail(target: T, url: String) {

        // присовили значение в Map
        requestMap[target] = url

        // отправили сообщение в очередь Handler с помощью sendToTarget()
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