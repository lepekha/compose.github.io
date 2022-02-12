package ua.com.compose.di

import ua.com.compose.core.models.data.MenuObjects
import ua.com.compose.view.main.MainPresenter
import ua.com.compose.view.main.main.PresenterMain
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ua.com.compose.view.main.main.ImageHolder

val appModule = module {

    scope(named("app")) {
        scoped { ImageHolder() }
        scoped { MainPresenter() }
        scoped { PresenterMain(menu = get(), context = androidContext(), imageHolder = get()) }
        scoped { MenuObjects(get()) }
    }
}