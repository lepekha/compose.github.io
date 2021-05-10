package ua.com.compose.instagram_no_crop.view.save

import android.graphics.Bitmap
import android.net.Uri
import ua.com.compose.mvp.BaseMvpView


interface InstagramCropSaveView : BaseMvpView {
    fun setImageBitmap(bitmap: Bitmap)
    fun createShareIntent(uri: Uri)
    fun createInstagramIntent(uri: Uri)
    fun setImageBackground(bitmap: Bitmap)
    fun setVisibleEdit(isVisible: Boolean)
}