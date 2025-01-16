package com.example.photogallery

import android.util.Log
import android.view.View
import android.view.ViewTreeObserver

class DynamicColumnCalculation {

    fun calculateDynamicColumn(view: View) {

        val listener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {

                val width = view.width
                val height = view.height

                Log.d(TAG, "width = $width\n" +
                        "height = $height")

            }
        }

        view.viewTreeObserver.addOnGlobalLayoutListener(listener)

    }

    companion object {
        private const val TAG = "DynamicColumnCalculation"
    }
}