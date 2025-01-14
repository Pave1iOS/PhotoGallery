package com.example.photogallery.DI

import android.app.Application
import com.example.photogallery.photoGalleryFragment.PhotoGalleryFragment
import dagger.BindsInstance
import dagger.Component

@Component(modules = [NetworkModule::class, ViewModelModule::class])
interface AppComponent {

    fun inject(photoGalleryFragment: PhotoGalleryFragment)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): AppComponent
    }
}