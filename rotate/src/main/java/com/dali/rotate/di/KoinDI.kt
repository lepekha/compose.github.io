package com.dali.rotate.di

import com.dali.rotate.view.main.ImageRotatePresenter
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.getKoin

internal object Scope{
    val ROTATE
        get() = getKoin().getOrCreateScope(
                "ROTATE", named("ROTATE"))
}

val rotateModule = module {

    scope(named("ROTATE")) {
        scoped { ImageRotatePresenter(context = androidContext()) }
    }
}