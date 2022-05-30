package ua.com.compose.other_social_media_crop.di

import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.getKoin
import ua.com.compose.other_social_media_crop.main.SocialMediaCropPresenter

internal object Scope{
    val IMAGE
        get() = getKoin().getOrCreateScope(
                "IMAGE", named("IMAGE"))
}

val otherSocialMediaCropModule = module {

    scope(named("IMAGE")) {
        scoped { SocialMediaCropPresenter(context = androidContext()) }
    }
}