package ua.com.compose.image_style.di

import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.getKoin
import ua.com.compose.image_style.style.ImageStylePresenter

internal object Scope{
    val IMAGE_STYLE
        get() = getKoin().getOrCreateScope(
                "IMAGE_STYLE", named("IMAGE_STYLE"))
}

val imageStyleModule = module {
    scope(named("IMAGE_STYLE")) {
        scoped { ImageStylePresenter(context = androidContext()) }
    }
}