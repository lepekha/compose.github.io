package com.inhelp.grids.main

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.inhelp.base.mvp.BaseMvpPresenterImpl
import com.inhelp.extension.saveBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class GridPresenter(private val context: Context): BaseMvpPresenterImpl<GridView>() {

    private var currentUri: Uri? = null

    fun pressSave(bitmap: Bitmap) = CoroutineScope(Dispatchers.Main).launch {
//        if(!isLoadPress){
//            isLoadPress = true
//            view?.setDownloadProgressVisible(isVisible = true)
        withContext(Dispatchers.IO) {
            context.saveBitmap(bitmap)
        }
//            view?.setDownloadProgressVisible(isVisible = false)
//        }
    }

    fun onLoad(uriString: String?){
        val currentUri = this.currentUri
        when{
            (uriString != null) -> {
                this.currentUri = Uri.parse(uriString)
                view?.setImage(Uri.parse(uriString))
            }
            (currentUri != null) -> {
                view?.setImage(currentUri)
            }
            else -> {
                view?.backToMain()
            }
        }
    }

}