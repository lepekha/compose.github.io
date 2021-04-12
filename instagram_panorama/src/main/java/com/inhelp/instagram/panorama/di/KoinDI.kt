package com.inhelp.instagram.panorama.di

import com.inhelp.instagram.panorama.data.TransferObject
import com.inhelp.instagram.panorama.view.main.InstagramPanoramaPresenter
import com.inhelp.instagram.panorama.view.save.InstagramPanoramaSavePresenter
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.getKoin

object Scope{
    val INSTAGRAM
        get() = getKoin().getOrCreateScope(
                "INSTAGRAM", named("INSTAGRAM"))
}

val instagamPanoramaModule = module {

    scope(named("INSTAGRAM")) {
        scoped { TransferObject() }
        scoped { InstagramPanoramaSavePresenter(context = androidContext(), transferObject = get()) }
        scoped { InstagramPanoramaPresenter(transferObject = get()) }
    }
}