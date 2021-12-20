package ua.com.compose.image_filter.di

import ua.com.compose.image_filter.main.ImageFilterPresenter
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.getKoin
import ua.com.compose.image_filter.db.ImageStyleDatabase
import ua.com.compose.image_filter.style.ImageStylePresenter

internal object Scope{
    val IMAGE_FILTER
        get() = getKoin().getOrCreateScope(
                "IMAGE_FILTER", named("IMAGE_FILTER"))

    val IMAGE_STYLE
        get() = getKoin().getOrCreateScope(
                "IMAGE_STYLE", named("IMAGE_STYLE"))
}

val imageFilterModule = module {

    scope(named("IMAGE_FILTER")) {
        scoped { ImageFilterPresenter(context = androidContext()) }
    }

    scope(named("IMAGE_STYLE")) {
        scoped { ImageStyleDatabase(context = androidContext()) }
        scoped { ImageStylePresenter(context = androidContext(), database = get()) }
    }
}