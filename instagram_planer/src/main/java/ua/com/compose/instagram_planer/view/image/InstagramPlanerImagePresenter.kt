package ua.com.compose.instagram_planer.view.image

import android.content.Context
import android.net.Uri
import ua.com.compose.instagram_planer.view.main.InstagramPlanerPresenter
import ua.com.compose.mvp.BaseMvpPresenterImpl


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