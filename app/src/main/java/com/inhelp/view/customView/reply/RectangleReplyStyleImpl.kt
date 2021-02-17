package com.inhelp.view.customView.reply

import android.graphics.*
import android.text.TextPaint
import com.inhelp.utils.extension.dp

class RectangleReplyStyleImpl(
        val title: String
) : ReplyImageStyle(), ReplyStyle {

    private constructor(builder: Builder) : this(builder.title)

    companion object {
        inline fun build(block: Builder.() -> Unit) = Builder().apply(block).build()
    }

    class Builder {
        var title: String = "InHelp"

        fun build() = RectangleReplyStyleImpl(this)
    }



    private val bgPaint: Paint by lazy {
        val p = Paint()
        p.color = Color.WHITE
        p
    }

    private val textPaint: TextPaint by lazy {
        val p = TextPaint()
        p.color = Color.BLACK
        p.textSize = 14.dp
        p
    }

    private val mTextForDraw = "Repost: $title"

    private val mRadius = 4.dp

    override fun generateImage(image: Bitmap): Bitmap {
        val height = Math.abs(textPaint.fontMetrics.ascent) + Math.abs(textPaint.fontMetrics.descent)

        mImage = Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888)
        val repostImage = Bitmap.createBitmap((textPaint.measureText(mTextForDraw) + 10.dp).toInt(), (height + 10.dp).toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(mImage)
        val repostCanvas = Canvas(repostImage)
        canvas.drawBitmap(image, 0f, 0f, bgPaint)
        repostCanvas.drawRoundRect( 0f, 0f, textPaint.measureText(mTextForDraw) + 10.dp, height + 10.dp, mRadius, mRadius, bgPaint)
        repostCanvas.drawText(mTextForDraw, 5.dp ,  height + 5.dp /2, textPaint)

        canvas.drawBitmap(image, 0f, 0f, bgPaint)
        canvas.drawBitmap(
                repostImage,
                EReplyPosition.START_BOTTOM.getLeft(mImage, repostImage),
                EReplyPosition.START_BOTTOM.getTop(mImage, repostImage),
                bgPaint)
        return mImage
    }

}