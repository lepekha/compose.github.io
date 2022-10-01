package ua.com.compose.di

import ua.com.compose.view.main.MainPresenter
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {

    scope(named("app")) {
        scoped { MainPresenter() }
    }
}