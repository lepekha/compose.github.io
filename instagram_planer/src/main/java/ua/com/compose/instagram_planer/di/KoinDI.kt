package ua.com.compose.instagram_planer.di
import ua.com.compose.instagram_planer.view.image.InstagramPlanerImagePresenter
import ua.com.compose.instagram_planer.view.main.InstagramPlanerPresenter
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.getKoin

object Scope{
    val INSTAGRAM
        get() = getKoin().getOrCreateScope(
                "INSTAGRAM", named("INSTAGRAM"))
}

val instagramPlanerModule = module {

    scope(named("INSTAGRAM")) {
        scoped { InstagramPlanerPresenter(context = androidContext()) }
        scoped { InstagramPlanerImagePresenter(context = androidContext(), presenter = get()) }
    }
}