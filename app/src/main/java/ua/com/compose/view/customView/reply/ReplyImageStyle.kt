package ua.com.compose.view.customView.reply

import android.graphics.Bitmap
import android.graphics.RectF
import ua.com.compose.utils.extension.percent

open class ReplyImageStyle {
    var mReplyPosition = EReplyPosition.END_BOTTOM
    lateinit var mImage: Bitmap

    val mRepostHeight: Float
        get() = mImage.height.percent(7).toFloat()
    val mRepostWidth: Float
        get() = mImage.width.percent(40).toFloat()

    val rectForDraw: RectF
        get () = when (mReplyPosition) {
            EReplyPosition.START_TOP -> RectF(0f, 0f, mRepostWidth, mRepostHeight)
            EReplyPosition.START_BOTTOM -> RectF(0f, mImage.height - mRepostHeight, mRepostWidth, mImage.height.toFloat())
            EReplyPosition.END_TOP -> RectF(mImage.width - mRepostWidth, 0f, mImage.width.toFloat(), mRepostHeight)
            EReplyPosition.END_BOTTOM -> RectF(mImage.width - mRepostWidth, mImage.height - mRepostHeight, mImage.width.toFloat(), mImage.height.toFloat())
        }
}