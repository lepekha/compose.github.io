package com.inhelp.instagram.grid.di

import com.inhelp.instagram.grid.view.save.InstagramGridSavePresenter
import com.inhelp.instagram.grid.view.main.InstagramGridPresenter
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.getKoin

object Scope{
    val INSTAGRAM
        get() = getKoin().getOrCreateScope(
                "INSTAGRAM", named("INSTAGRAM"))
}

val instagramGridModule = module {

    scope(named("INSTAGRAM")) {
        scoped { InstagramGridSavePresenter(context = androidContext(), presenter = get()) }
        scoped { InstagramGridPresenter() }
    }
}