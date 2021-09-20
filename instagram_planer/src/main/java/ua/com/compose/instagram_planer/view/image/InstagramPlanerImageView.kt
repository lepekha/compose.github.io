package ua.com.compose.instagram_planer.view.image

import android.net.Uri
import ua.com.compose.mvp.BaseMvpView


interface InstagramPlanerImageView : BaseMvpView {
    fun setImage(uri: Uri)
}