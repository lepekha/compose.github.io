package ua.com.compose.other_tags.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ua.com.compose.other_tags.main.PresenterTags

val tagsModule = module {
    factory { PresenterTags(context = androidContext()) }
}