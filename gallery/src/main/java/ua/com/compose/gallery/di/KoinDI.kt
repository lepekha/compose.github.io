package ua.com.compose.gallery.di

import ua.com.compose.gallery.main.PresenterGallery
import ua.com.compose.gallery.main.content.PresenterGalleryContent
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val galleryModule = module {

    scope(named("gallery")) {
        scoped { PresenterGallery(context = androidContext()) }
        factory { PresenterGalleryContent(context = androidContext(), presenterGallery = get()) }
    }
}