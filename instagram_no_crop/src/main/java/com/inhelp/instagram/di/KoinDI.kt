package com.inhelp.instagram.di

import com.inhelp.instagram.view.main.InstagramCropPresenter
import com.inhelp.instagram.view.save.InstagramCropSavePresenter
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.getKoin

object Scope{
    val INSTAGRAM
        get() = getKoin().getOrCreateScope(
                "INSTAGRAM", named("INSTAGRAM"))
}

val instagramCropModule = module {

    scope(named("INSTAGRAM")) {
        scoped { InstagramCropPresenter() }
        scoped { InstagramCropSavePresenter(context = androidContext(), presenter = get()) }
    }
}