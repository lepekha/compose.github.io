package ua.com.compose.di

import ua.com.compose.core.models.data.MenuObjects
import ua.com.compose.view.main.MainPresenter
import ua.com.compose.view.main.main.PresenterMain
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {

    scope(named("app")) {
        scoped { MainPresenter() }
        scoped { PresenterMain(menu = get()) }
        scoped { MenuObjects(context = androidContext()) }
    }
}