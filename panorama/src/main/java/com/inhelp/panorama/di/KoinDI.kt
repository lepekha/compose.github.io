package com.inhelp.panorama.di

import com.inhelp.panorama.data.TransferObject
import com.inhelp.panorama.view.main.PanoramaPresenter
import com.inhelp.panorama.view.save.PanoramaSavePresenter
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val panoramaModule = module {
    scope(named("panorama")) {
        scoped { TransferObject() }
        scoped { PanoramaPresenter(transferObject = get()) }
        scoped { PanoramaSavePresenter(context = androidContext(), transferObject = get()) }
    }
}