package ua.com.compose.view.customView.reply

import android.graphics.Bitmap

enum class EReplyPosition {
    START_TOP{
        override fun getLeft(image: Bitmap, repostImage: Bitmap) = 0f
        override fun getTop(image: Bitmap, repostImage: Bitmap) = 0f
    },
    END_TOP{
        override fun getLeft(image: Bitmap, repostImage: Bitmap) = (image.width - repostImage.width).toFloat()
        override fun getTop(image: Bitmap, repostImage: Bitmap) = 0f
    },
    START_BOTTOM{
        override fun getLeft(image: Bitmap, repostImage: Bitmap) = 0f
        override fun getTop(image: Bitmap, repostImage: Bitmap) = (image.height - repostImage.height).toFloat()
    },
    END_BOTTOM{
        override fun getLeft(image: Bitmap, repostImage: Bitmap) = (image.width - repostImage.width).toFloat()
        override fun getTop(image: Bitmap, repostImage: Bitmap) = (image.height - repostImage.height).toFloat()
    };

    abstract fun getLeft(image: Bitmap, repostImage: Bitmap): Float
    abstract fun getTop(image: Bitmap, repostImage: Bitmap): Float
}