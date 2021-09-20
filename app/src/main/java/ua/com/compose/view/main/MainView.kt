package ua.com.compose.view.main

import android.net.Uri
import ua.com.compose.mvp.BaseMvpView


interface MainView : BaseMvpView {
    fun finishApplication()
}