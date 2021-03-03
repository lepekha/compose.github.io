package com.inhelp.grids.di

import com.inhelp.grids.main.GridPresenter
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val gridModule = module {
    factory { GridPresenter(context = androidContext()) }
}