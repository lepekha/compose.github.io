package com.inhelp.crop.di

import com.inhelp.crop.data.TransferObject
import com.inhelp.crop.view.edit.CropEditPresenter
import com.inhelp.crop.view.main.CropPresenter
import com.inhelp.crop.view.share.CropSharePresenter
import com.inhelp.gallery.main.PresenterGallery
import com.inhelp.gallery.main.content.PresenterGalleryContent
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