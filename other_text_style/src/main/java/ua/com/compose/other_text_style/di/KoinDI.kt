package ua.com.compose.other_text_style.di

import ua.com.compose.other_text_style.main.TextStylePresenter
import org.koin.dsl.module

val textStyleModule = module {
    factory { TextStylePresenter() }
}