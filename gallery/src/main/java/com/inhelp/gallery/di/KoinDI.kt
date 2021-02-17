package com.inhelp.gallery.di

import com.inhelp.gallery.main.PresenterGallery
import com.inhelp.gallery.main.content.PresenterGalleryContent
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val galleryModule = module {

    scope(named("gallery")) {
        scoped { PresenterGallery(context = androidContext()) }
        factory { PresenterGalleryContent(context = androidContext(), presenterGallery = get()) }
    }
}