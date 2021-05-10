package ua.com.compose.instagram_grid.di

import ua.com.compose.instagram_grid.view.save.InstagramGridSavePresenter
import ua.com.compose.instagram_grid.view.main.InstagramGridPresenter
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.getKoin

object Scope{
    val INSTAGRAM
        get() = getKoin().getOrCreateScope(
                "INSTAGRAM", named("INSTAGRAM"))
}

val instagramGridModule = module {

    scope(named("INSTAGRAM")) {
        scoped { InstagramGridSavePresenter(context = androidContext(), presenter = get()) }
        scoped { InstagramGridPresenter() }
    }
}