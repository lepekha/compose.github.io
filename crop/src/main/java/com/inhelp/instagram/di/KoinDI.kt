package com.inhelp.instagram.di

import com.inhelp.instagram.data.TransferObject
import com.inhelp.instagram.view.edit.CropEditPresenter
import com.inhelp.instagram.view.main.CropPresenter
import com.inhelp.instagram.view.share.CropSharePresenter
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val cropModule = module {

    scope(named("crop")) {
        scoped { TransferObject() }
        scoped { CropPresenter(transferObject = get()) }
        scoped { CropEditPresenter(context = androidContext(), transferObject = get()) }
        scoped { CropSharePresenter(context = androidContext(), transferObject = get()) }
    }
}