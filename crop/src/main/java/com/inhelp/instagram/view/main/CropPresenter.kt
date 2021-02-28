package com.inhelp.instagram.view.main

import android.graphics.Bitmap
import android.net.Uri
import com.inhelp.base.mvp.BaseMvpPresenterImpl
import com.inhelp.instagram.data.TransferObject


class CropPresenter(val transferObject: TransferObject): BaseMvpPresenterImpl<CropView>() {

    private var currentUri: Uri? = null

    fun pressCrop(bitmap: Bitmap){
        transferObject.image = bitmap
        view?.navigateToCropEdit()
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