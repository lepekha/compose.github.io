package ua.com.compose.image_crop.di

import ua.com.compose.image_crop.main.ImageCropPresenter
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.getKoin

internal object Scope{
    val IMAGE
        get() = getKoin().getOrCreateScope(
                "IMAGE", named("IMAGE"))
}

val imageCropModule = module {

    scope(named("IMAGE")) {
        scoped { ImageCropPresenter(context = androidContext()) }
    }
}