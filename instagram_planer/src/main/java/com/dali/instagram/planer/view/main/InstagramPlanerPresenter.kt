package com.dali.instagram.planer.view.main

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.dali.file.FileStorage
import com.inhelp.base.mvp.BaseMvpPresenterImpl
import java.io.File


class InstagramPlanerPresenter(val context: Context): BaseMvpPresenterImpl<InstagramPlanerView>() {


    val images = mutableListOf<Uri>()

//    private suspend fun loadImage() = withContext(Dispatchers.IO) {
//        transferObject.images.reversed().forEachIndexed { index, bitmap ->
//            context.saveBitmap(bitmap, "${index}_")
//        }
//    }

    fun onLoad(uriString: String?){
        when{
            (uriString != null) -> {
                val uri = Uri.parse(uriString)
                images.add(0, uri)
                FileStorage.copyFileToDir(file = File(getPath(uri)), dirName = "planer/ruslan", fileName = images.size.toString())
            }
        }
        val images = FileStorage.readFilesFromDir(dirName = "planer/ruslan")
        this.images.addAll(images)
        view?.updateList()
    }

    fun getPath(uri: Uri): String {
        val projection = arrayOf<String>(MediaStore.Images.Media.DATA)
        val cursor: Cursor = context.getContentResolver().query(uri, projection, null, null, null) ?: return ""
        val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val s: String = cursor.getString(column_index)
        cursor.close()
        return s
    }

//    fun pressImage(position: Int) = CoroutineScope(Main).launch {
//        gridImages.getOrNull(gridImages.size - position + 1)?.scale(512, 512, false)?.let { bitmap ->
//            val uri = withContext(Dispatchers.IO) { context.createTempUri(bitmap) }
//            view?.createInstagramIntent(uri = uri)
//        }
//    }
}