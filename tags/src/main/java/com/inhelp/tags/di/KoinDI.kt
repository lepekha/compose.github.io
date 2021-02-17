package com.inhelp.tags.di

import com.inhelp.tags.main.PresenterTags
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val tagsModule = module {
    factory { PresenterTags(context = androidContext()) }
}