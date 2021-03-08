package com.inhelp.instagram.grid.di

import com.inhelp.instagram.grid.data.TransferObject
import com.inhelp.instagram.grid.view.save.GridSavePresenter
import com.inhelp.instagram.grid.view.main.GridPresenter
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.getKoin

object Scope{
    val INSTAGRAM
        get() = getKoin().getOrCreateScope(
                "INSTAGRAM", named("INSTAGRAM"))
}

val gridModule = module {

    scope(named("INSTAGRAM")) {
        scoped { TransferObject() }
        scoped { GridSavePresenter(context = androidContext(), transferObject = get()) }
        scoped { GridPresenter(transferObject = get()) }
    }
}