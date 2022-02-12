package ua.com.compose.view.main.main

import android.content.Context
import android.provider.MediaStore
import android.provider.MediaStore.MediaColumns
import android.database.Cursor
import android.net.Uri
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.com.compose.R
import ua.com.compose.analytics.Analytics
import ua.com.compose.analytics.Event
import ua.com.compose.analytics.SimpleEvent
import ua.com.compose.analytics.analytics
import ua.com.compose.mvp.BaseMvpPresenterImpl
import ua.com.compose.core.models.data.MenuObjects
import ua.com.compose.dialog.DialogManager
import ua.com.compose.extension.createImageIntent
import ua.com.compose.extension.loadImage
import ua.com.compose.extension.saveBitmap


class PresenterMain(private val menu: MenuObjects, val context: Context, val imageHolder: ImageHolder): BaseMvpPresenterImpl<ViewMain>() {

    val photoList = mutableListOf<String>()

    override fun attachView(view: ViewMain) {
        super.attachView(view)
//        photoList.addAll(getImagesPath(view.getCurrentContext()))
    }

    fun getOrCreateMenu(fm: FragmentManager?) = menu.getOrCreateMenu(fm)

    fun onCreate(uri: Uri?){
        addImage(uri ?: imageHolder.image)
        view?.setVisibleImage(isVisible = imageHolder.image != null)
    }

    fun addImage(uri: Uri?) {
        val it = uri ?: return
        imageHolder.image = it
        view?.setImage(it)
    }

    fun pressSave() = launch(Dispatchers.Main) {
        val uri = imageHolder.image ?: return@launch
        DialogManager.createLoad{}.apply {
            withContext(Dispatchers.IO) {
                val bitmap = context.loadImage(uri)
                context.saveBitmap(bitmap)
            }
            analytics.send(event = SimpleEvent(
                key = Analytics.Event.IMAGE_SAVE
            ))
            view?.showAlert(R.string.module_image_crop_fragment_image_crop_save_ready)
            this.closeDialog()
        }
    }

    fun pressRemove() = launch(Dispatchers.Main) {
        withContext(Dispatchers.IO) {
            imageHolder.image = null
        }
        view?.setVisibleImage(false)
    }

    fun updateList(){
        view?.updatePhotoList()
    }
}

