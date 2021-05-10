package ua.com.compose.instagram_panorama.di

import ua.com.compose.instagram_panorama.view.main.InstagramPanoramaPresenter
import ua.com.compose.instagram_panorama.view.save.InstagramPanoramaSavePresenter
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.getKoin

object Scope{
    val INSTAGRAM
        get() = getKoin().getOrCreateScope(
                "INSTAGRAM", named("INSTAGRAM"))
}

val instagamPanoramaModule = module {
    scope(named("INSTAGRAM")) {
        scoped { InstagramPanoramaSavePresenter(context = androidContext(), prevPresenter = get()) }
        scoped { InstagramPanoramaPresenter() }
    }
}