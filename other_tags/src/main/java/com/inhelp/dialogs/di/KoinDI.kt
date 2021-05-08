package com.inhelp.dialogs.di

import com.inhelp.dialogs.main.PresenterTags
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val tagsModule = module {
    factory { PresenterTags(context = androidContext()) }
}