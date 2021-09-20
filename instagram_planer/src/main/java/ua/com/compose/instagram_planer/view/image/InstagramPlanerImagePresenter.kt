package ua.com.compose.instagram_planer.view.image

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.com.compose.file_storage.FileStorage
import ua.com.compose.instagram_planer.data.Image
import ua.com.compose.mvp.BaseMvpPresenterImpl
import java.io.File


class InstagramPlanerImagePresenter(val context: Context): BaseMvpPresenterImpl<InstagramPlanerImageView>() {

    private var image: Image? = null

    fun onCreate(){
//        image = presenter.currentImage?.apply {
//            view?.setImage(uri = this.uri.toUri())
//        }
    }

    fun pressDelete() = CoroutineScope(Dispatchers.Main).launch {
        withContext(Dispatchers.IO) {
            image?.let {
//                presenter.imageDao?.delete(image = it)
//                FileStorage.removeFile(File(presenter.getPath(it.uri.toUri())))
            }
        }
        onBackPress()
    }

    fun onAddImages(imageUris: List<Uri>) = CoroutineScope(Dispatchers.Main).launch {
        withContext(Dispatchers.IO) {
            imageUris.firstOrNull()?.apply {
//                val uri = FileStorage.copyFileToDir(file = File(presenter.getPath(this)), dirName = presenter.userFolder, fileName = this.hashCode().toString())
                image?.apply {
                    this.uri = uri.toString()
//                    presenter.imageDao?.update(image = this)
                }
            }
        }?.let {
            view?.setImage(it)
        }
    }
}