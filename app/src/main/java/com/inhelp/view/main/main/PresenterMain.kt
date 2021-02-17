package com.inhelp.view.main.main

import android.content.Context
import com.inhelp.core.models.data.Menu
import android.provider.MediaStore
import android.provider.MediaStore.MediaColumns
import android.database.Cursor
import android.net.Uri
import com.inhelp.base.mvp.BaseMvpPresenterImpl


class PresenterMain: BaseMvpPresenterImpl<ViewMain>() {

    val menuList = Menu.getMenuList()
    val photoList = mutableListOf<String>()

    override fun attachView(view: ViewMain) {
        super.attachView(view)
//        photoList.addAll(getImagesPath(view.getCurrentContext()))
    }

    fun pressSave() {
    }


    fun updateList(){
        view?.updatePhotoList()
    }


//    fun getCameraImages(context: Context): List<String> {
//        val projection = arrayOf(MediaStore.Images.Media.DATA)
//        val selection = MediaStore.Images.Media.BUCKET_ID + " = ?"
//        val selectionArgs = arrayOf<String>(CAMERA_IMAGE_BUCKET_ID)
//        val cursor = context.getContentResolver().query(Images.Media.EXTERNAL_CONTENT_URI,
//                projection,
//                selection,
//                selectionArgs,
//                null)
//        val result = ArrayList<String>(cursor.getCount())
//        if (cursor.moveToFirst()) {
//            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//            do {
//                val data = cursor.getString(dataColumn)
//                result.add(data)
//            } while (cursor.moveToNext())
//        }
//        cursor.close()
//        return result
//    }

    fun getImagesPath(context: Context): MutableList<String> {
        val uri: Uri
        val listOfAllImages = ArrayList<String>()
        val cursor: Cursor?
        val column_index_data: Int
        val column_index_folder_name: Int
        var PathOfImage: String? = null
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

        cursor = context.contentResolver.query(uri, projection, null, null, null)

        column_index_data = cursor!!.getColumnIndexOrThrow(MediaColumns.DATA)
        column_index_folder_name = cursor!!
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        while (cursor!!.moveToNext()) {
            PathOfImage = cursor!!.getString(column_index_data)

            listOfAllImages.add(PathOfImage)
        }
        return listOfAllImages
    }
}

