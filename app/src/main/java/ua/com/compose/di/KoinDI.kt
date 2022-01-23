package ua.com.compose.di

import ua.com.compose.core.models.data.MenuObjects
import ua.com.compose.view.main.MainPresenter
import ua.com.compose.view.main.main.PresenterMain
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ua.com.compose.view.main.info.ImageInfoViewModule

val appModule = module {

    scope(named("app")) {
        scoped { MainPresenter() }
        scoped { PresenterMain(menu = get()) }
        scoped { MenuObjects(presenter = get()) }
        viewModel { ImageInfoViewModule(context = androidContext()) }
    }
}