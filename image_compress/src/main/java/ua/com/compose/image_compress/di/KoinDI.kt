package ua.com.compose.image_compress.di

import ua.com.compose.image_compress.main.ImageCompressPresenter
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.getKoin

internal object Scope{
    val IMAGE_COMPRESS
        get() = getKoin().getOrCreateScope(
                "IMAGE_COMPRESS", named("IMAGE_COMPRESS"))
}

val imageCompressModule = module {

    scope(named("IMAGE_COMPRESS")) {
        scoped { ImageCompressPresenter(context = androidContext()) }
    }
}