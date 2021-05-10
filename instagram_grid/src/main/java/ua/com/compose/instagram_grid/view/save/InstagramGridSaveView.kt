package ua.com.compose.instagram_grid.view.save

import android.net.Uri
import ua.com.compose.mvp.BaseMvpView


interface InstagramGridSaveView : BaseMvpView {
    fun createInstagramIntent(uri: Uri)
}