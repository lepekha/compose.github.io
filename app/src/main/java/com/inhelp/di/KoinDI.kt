package com.inhelp.di

import com.inhelp.core.models.InstagramUrlData
import com.inhelp.view.main.MainPresenter
import com.inhelp.view.main.main.PresenterMain
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    factory { MainPresenter() }
    factory { PresenterMain() }
    single { InstagramUrlData(androidContext()) }
}