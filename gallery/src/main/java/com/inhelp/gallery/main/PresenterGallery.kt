package com.inhelp.gallery.main

import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import com.inhelp.base.mvp.BaseMvpPresenterImpl
import kotlinx.coroutines.*
import java.io.File
import java.net.URI

class PresenterGallery(val context: Context) : BaseMvpPresenterImpl<ViewGallery>() {

    val folders = mutableListOf<ImageFolder>()

    override fun attachView(view: ViewGallery) {
        super.attachView(view)
    }

    fun getAllShownImagesPath(activity: Activity)= CoroutineScope(Dispatchers.IO).launch {
        val uriExternal: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val imageFolders = mutableMapOf<String, ImageFolder>()
        val projection = arrayOf(MediaStore.Images.Thumbnails._ID, MediaStore.Images.Thumbnails.DATA)
        val cursor = activity.contentResolver.query(uriExternal, projection, null, null, null)
        if (cursor != null) {
            val columnIndexID = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID)
            val columnDataID = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA)
            while (cursor.moveToNext()) {
                val imageId = cursor.getLong(columnIndexID)
                val dirName = (File(cursor.getString(columnDataID)).parentFile as File).name
                val uriImage = Uri.withAppendedPath(uriExternal, "" + imageId)
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
        imageFolders.values.onEach { it.images.reverse() }

        val allFolder = ImageFolder().apply {
            this.name = "All"
            imageFolders.values.forEach {
                this.images.addAll(it.images)
            }
        }
        folders.add(allFolder)
        folders.sortBy { it.name }

        withContext(Dispatchers.Main){
            view?.setVisibleTabs(isVisible = folders.size > 2)
            view?.updateAllList()
        }
    }

    fun pressImage(uri: Uri){
        view?.passData(uri.toString())
        onBackPress()
    }
}

class ImageFolder{
    var name: String = ""
    val images: MutableList<Uri> = mutableListOf()
}