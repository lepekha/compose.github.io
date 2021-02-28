package com.inhelp.grids.share

import android.net.Uri
import com.inhelp.base.mvp.BaseMvpPresenterImpl



class GridSharePresenter: BaseMvpPresenterImpl<GridShareView>() {

    private var currentUri: Uri? = null

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