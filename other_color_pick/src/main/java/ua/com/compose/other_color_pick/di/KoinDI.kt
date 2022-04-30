package ua.com.compose.other_color_pick.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel
import ua.com.compose.other_color_pick.main.ImageInfoViewModule

val otherColorPickModule = module {
    viewModel { ImageInfoViewModule(androidContext()) }
}