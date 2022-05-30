package ua.com.composeimport android.app.Applicationimport android.content.Contextimport ua.com.compose.instagram_planer.di.instagramPlanerModuleimport ua.com.compose.image_crop.di.imageCropModuleimport ua.com.compose.gallery.di.galleryModuleimport ua.com.compose.instagram_no_crop.di.instagramCropModuleimport ua.com.compose.instagram_grid.di.instagramGridModuleimport ua.com.compose.instagram_panorama.di.instagamPanoramaModuleimport org.koin.android.ext.koin.androidContextimport org.koin.core.context.startKoinimport ua.com.compose.analytics.AnalyticsImplimport ua.com.compose.analytics.analyticsimport ua.com.compose.config.RemoteConfigImplimport ua.com.compose.config.remoteConfigimport ua.com.compose.di.appModuleimport ua.com.compose.extension.prefsimport ua.com.compose.image_compress.di.imageCompressModuleimport ua.com.compose.image_filter.di.imageFilterModuleimport ua.com.compose.image_rotate.di.rotateModuleimport ua.com.compose.image_style.di.imageStyleModuleimport ua.com.compose.other_color_pick.di.otherColorPickModuleimport ua.com.compose.other_image_info.di.otherImageInfoModuleimport ua.com.compose.other_social_media_crop.di.otherSocialMediaCropModuleimport ua.com.compose.other_text_style.di.textStyleModuleclass MainApp : Application() {    override fun onCreate() {        super.onCreate()        prefs = getSharedPreferences("compose", Context.MODE_PRIVATE)        analytics = AnalyticsImpl(context = applicationContext)        remoteConfig = RemoteConfigImpl()        startKoin{            androidContext(this@MainApp)            modules(listOf(                    appModule,                    imageFilterModule,                    imageStyleModule,                    imageCropModule,                    rotateModule,                    imageCompressModule,                    textStyleModule,                    galleryModule,                    instagamPanoramaModule,                    instagramGridModule,                    instagramCropModule,                    instagramPlanerModule,                    otherImageInfoModule,                    otherSocialMediaCropModule,                    otherColorPickModule            ))        }    }}