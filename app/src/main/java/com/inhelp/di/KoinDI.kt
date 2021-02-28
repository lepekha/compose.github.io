package com.inhelp.di

import com.inhelp.core.models.data.MenuObjects
import com.inhelp.view.main.MainPresenter
import com.inhelp.view.main.main.PresenterMain
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