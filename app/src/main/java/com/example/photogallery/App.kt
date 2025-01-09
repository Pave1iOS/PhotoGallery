package com.example.photogallery

import android.app.Application
import com.example.photogallery.DI.AppComponent
import com.example.photogallery.DI.DaggerAppComponent

class App: Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.create()
    }
}