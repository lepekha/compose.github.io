package com.inhelp.crop.di

import com.inhelp.crop.view.main.CropPresenter
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.getKoin

internal object Scope{
    val CROP
        get() = getKoin().getOrCreateScope(
                "CROP", named("CROP"))
}

val cropModule = module {

    scope(named("CROP")) {
        scoped { CropPresenter(context = androidContext()) }
    }
}