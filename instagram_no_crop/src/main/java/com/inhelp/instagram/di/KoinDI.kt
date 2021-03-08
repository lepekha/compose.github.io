package com.inhelp.instagram.di

import com.inhelp.instagram.data.TransferObject
import com.inhelp.instagram.view.edit.NoCropEditPresenter
import com.inhelp.instagram.view.main.MainPresenter
import com.inhelp.instagram.view.save.NoCropSavePresenter
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.getKoin

object Scope{
    val INSTAGRAM
        get() = getKoin().getOrCreateScope(
                "INSTAGRAM", named("INSTAGRAM"))
}

val noCropModule = module {

    scope(named("INSTAGRAM")) {
        scoped { TransferObject() }
        scoped { MainPresenter(transferObject = get()) }
        scoped { NoCropEditPresenter(context = androidContext(), transferObject = get()) }
        scoped { NoCropSavePresenter(context = androidContext(), transferObject = get()) }
    }
}