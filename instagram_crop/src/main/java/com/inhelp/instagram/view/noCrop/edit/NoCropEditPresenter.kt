package com.inhelp.instagram.view.noCrop.edit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import androidx.core.graphics.drawable.toBitmap
import com.inhelp.base.mvp.BaseMvpPresenterImpl
import com.inhelp.color.util.Util
import com.inhelp.instagram.data.TransferObject
import kotlin.math.max


class NoCropEditPresenter(val context: Context, val transferObject: TransferObject) : BaseMvpPresenterImpl<NoCropEditView>() {

    private var image: Bitmap? = null
    private var background: Bitmap? = null
    private var IMAGE_SIZE = 1024

    fun onLoadImage() {
        image = transferObject.images.firstOrNull() ?: image
        image?.let { bitmap ->
            IMAGE_SIZE = max(bitmap.width, bitmap.height)
            view?.setImageBitmap(bitmap = bitmap)
            view?.setVisibleEditButton(isVisible = bitmap.width != bitmap.height)
            background = Util.blur(context, bitmap.copy(Bitmap.Config.ARGB_8888, false)).apply {
                view?.setImageBackground(this)
            }
        }
    }

    fun onColorPick(color: Int) {
        background = ColorDrawable(color).toBitmap(width = IMAGE_SIZE, height = IMAGE_SIZE).apply {
            view?.setImageBackground(this)
        }
    }

    fun pressBlur(){
        val img = this.image ?: return
        background = Util.blur(context, img.copy(Bitmap.Config.ARGB_8888, false)).apply {
            view?.setImageBackground(this)
        }
    }

    fun pressDone() {
        val background = this.background ?: return
        val image = this.image ?: return
        val scaledBackground = Bitmap.createScaledBitmap(background.copy(Bitmap.Config.RGB_565, false), IMAGE_SIZE, IMAGE_SIZE, false)
        transferObject.images.clear()
        transferObject.images.add(Util.merge(scaledBackground, image))
        view?.navigateToShare()
    }
}