package com.inhelp.text_style.di

import com.inhelp.text_style.main.TextStylePresenter
import org.koin.dsl.module

val textStyleModule = module {
    factory { TextStylePresenter() }
}