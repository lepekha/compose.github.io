package com.inhelp.instagram.panorama.di

import com.inhelp.instagram.panorama.data.TransferObject
import com.inhelp.instagram.panorama.view.main.PanoramaPresenter
import com.inhelp.instagram.panorama.view.save.PanoramaSavePresenter
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.getKoin

object Scope{
    val INSTAGRAM
        get() = getKoin().getOrCreateScope(
                "INSTAGRAM", named("INSTAGRAM"))
}

val panoramaModule = module {

    scope(named("INSTAGRAM")) {
        scoped { TransferObject() }
        scoped { PanoramaSavePresenter(context = androidContext(), transferObject = get()) }
        scoped { PanoramaPresenter(transferObject = get()) }
    }
}