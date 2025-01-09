package com.example.photogallery.DI

import com.example.photogallery.photoGalleryFragment.PhotoGalleryFragment
import dagger.Component

@Component(modules = [NetworkModule::class])
interface AppComponent {

    fun injectPhotoFragment(fragment: PhotoGalleryFragment)

}