package ua.com.compose.gallery.di

import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ua.com.compose.gallery.main.PresenterGallery

val galleryModule = module {

    scope(named("gallery")) {
        scoped { PresenterGallery(context = androidContext()) }
    }
}