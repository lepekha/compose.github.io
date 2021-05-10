package ua.com.compose.image_rotate.di

import ua.com.compose.image_rotate.main.ImageRotatePresenter
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.getKoin

internal object Scope{
    val ROTATE
        get() = getKoin().getOrCreateScope(
                "ROTATE", named("ROTATE"))
}

val rotateModule = module {

    scope(named("ROTATE")) {
        scoped { ImageRotatePresenter(context = androidContext()) }
    }
}