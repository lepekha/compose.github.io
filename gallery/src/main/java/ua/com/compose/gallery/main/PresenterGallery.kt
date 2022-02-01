package ua.com.compose.gallery.main

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import ua.com.compose.mvp.bottomSheetFragment.BaseMvpDialogPresenterImpl
import kotlinx.coroutines.*
import ua.com.compose.gallery.R
import java.io.File

class PresenterGallery(val context: Context) : BaseMvpDialogPresenterImpl<ViewGallery>() {

    val images = mutableListOf<Uri>()
    val folders = mutableListOf<ImageFolder>()
    val selectedImages = mutableListOf<Uri>()
    var doneImages = mutableListOf<Uri>()
    private var _multiSelect = false
    var isMultiSelect: Boolean = false
        set(value) {
            field = value && _multiSelect
            view?.setVisibleButtons(field)
        }
        get() = field && _multiSelect

    fun onCreate(isMultiSelect: Boolean){
        _multiSelect = isMultiSelect
    }

    fun pressImage(uri: Uri, isMultiSelect: Boolean){
        this.isMultiSelect = isMultiSelect or this.isMultiSelect
        if(selectedImages.contains(uri)){
            selectedImages.remove(uri)
            if(selectedImages.isEmpty()){
                this.isMultiSelect = false
            }
        }else{
            selectedImages.add(uri)
        }

        if(!this.isMultiSelect && selectedImages.isNotEmpty()){
            addImage()
        }
    }

    fun pressFolder(value: ImageFolder){
        images.clear()
        images.addAll(value.images)
        view?.setFolderName(value.name)
        view?.initPhotos()
    }

    fun getAllShownImagesPath(activity: Activity)= CoroutineScope(Dispatchers.IO).launch {
        val uriExternal: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val allDirName = context.getString(R.string.module_gallery_fragment_all)
        val imageFolders = mutableMapOf<String, ImageFolder>().apply {
            this[allDirName] = ImageFolder().apply {
                this.name = allDirName
            }
        }
        val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATE_ADDED)
        val cursor = activity.contentResolver.query(uriExternal, projection, null, null, MediaStore.Images.Media.DATE_ADDED+" DESC")
        if (cursor != null) {
            val columnIndexID = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val columnBacketID = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            while (cursor.moveToNext()) {
                val imageId = cursor.getLong(columnIndexID)
                val dirName = cursor.getString(columnBacketID)
                val uriImage = Uri.withAppendedPath(uriExternal, "" + imageId)
                imageFolders[allDirName]?.images?.add(uriImage)
                (imageFolders[dirName] ?: ImageFolder()).apply {
                    this.name = dirName
                    this.images.add(uriImage)
                    imageFolders[dirName] = this
                }
            }
            cursor.close()
        }

        folders.clear()
        folders.addAll(imageFolders.values)
//        imageFolders.values.onEach { it.images.reverse() }

//        val allFolder = ImageFolder().apply {
//            this.name = context.getString(R.string.module_gallery_fragment_all)
//            imageFolders.values.forEach {
//                this.images.addAll(it.images)
//            }
//        }
//        folders.sortBy { it.name }
//        folders.add(0, allFolder)
        images.addAll(folders[0].images)
        withContext(Dispatchers.Main){
            view?.updateAllList()
        }
    }

    fun pressClear(){
        selectedImages.clear()
        isMultiSelect = false
        view?.clearSelect()
    }

    fun addImage(){
        doneImages = selectedImages
        view?.backPress()
    }
}

class ImageFolder{
    var name: String = ""
    val images: MutableList<Uri> = mutableListOf()
}