package ua.com.composeimport android.app.Applicationimport android.content.Contextimport org.koin.android.ext.koin.androidContextimport org.koin.core.context.startKoinimport ua.com.compose.api.analytics.AnalyticsImplimport ua.com.compose.api.analytics.analyticsimport ua.com.compose.di.appModuleimport ua.com.compose.extension.prefsclass MainApp : Application() {    override fun onCreate() {        super.onCreate()        prefs = getSharedPreferences("compose", Context.MODE_PRIVATE)        analytics = AnalyticsImpl(context = applicationContext)        startKoin{            androidContext(this@MainApp)            modules(listOf(                    appModule            ))        }    }}