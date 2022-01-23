package ua.com.compose.other_image_info.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel
import ua.com.compose.other_image_info.main.ImageInfoViewModule

val otherImageInfoModule = module {
    viewModel { ImageInfoViewModule(androidContext()) }
}