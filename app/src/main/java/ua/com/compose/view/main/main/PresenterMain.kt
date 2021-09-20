package ua.com.compose.view.main.main

import android.content.Context
import android.provider.MediaStore
import android.provider.MediaStore.MediaColumns
import android.database.Cursor
import android.net.Uri
import androidx.fragment.app.FragmentManager
import ua.com.compose.mvp.BaseMvpPresenterImpl
import ua.com.compose.core.models.data.MenuObjects


class PresenterMain(private val menu: MenuObjects): BaseMvpPresenterImpl<ViewMain>() {

    val photoList = mutableListOf<String>()

    override fun attachView(view: ViewMain) {
        super.attachView(view)
//        photoList.addAll(getImagesPath(view.getCurrentContext()))
    }

    fun getOrCreateMenu(fm: FragmentManager?) = menu.getOrCreateMenu(fm)

    fun pressSave() {
    }

    fun onCreate(){
//        uri?.let {
//            view?.setImage(it)
//        }
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
//                val ua.com.compose.mvp.data = cursor.getString(dataColumn)
//                result.add(ua.com.compose.mvp.data)
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

