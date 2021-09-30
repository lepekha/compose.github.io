package ua.com.compose.image_filter.di

import ua.com.compose.image_filter.main.ImageFilterPresenter
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.getKoin

internal object Scope{
    val IMAGE_FILTER
        get() = getKoin().getOrCreateScope(
                "IMAGE_FILTER", named("IMAGE_FILTER"))
}

val imageFilterModule = module {

    scope(named("IMAGE_FILTER")) {
        scoped { ImageFilterPresenter(context = androidContext()) }
    }
}