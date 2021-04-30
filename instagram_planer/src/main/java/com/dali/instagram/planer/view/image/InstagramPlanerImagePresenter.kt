package com.dali.instagram.planer.view.image

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.core.net.toFile
import com.dali.file.FileStorage
import com.dali.instagram.planer.view.main.InstagramPlanerPresenter
import com.inhelp.base.mvp.BaseMvpPresenterImpl
import java.io.File


class InstagramPlanerImagePresenter(val context: Context, val presenter: InstagramPlanerPresenter): BaseMvpPresenterImpl<InstagramPlanerImageView>() {

    fun pressDelete(){
        presenter.deleteImage()
        onBackPress()
    }

    fun onAddImages(imageUris: List<Uri>){
        imageUris.firstOrNull()?.let {
            presenter.changeImage(it)
        }
        view?.loadImage()
    }

}