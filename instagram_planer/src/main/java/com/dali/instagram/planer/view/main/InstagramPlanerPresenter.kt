package com.dali.instagram.planer.view.main

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.core.net.toFile
import androidx.room.Room
import com.dali.file.FileStorage
import com.dali.instagram.planer.data.AppDatabase
import com.dali.instagram.planer.data.User
import com.inhelp.base.mvp.BaseMvpPresenterImpl
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import java.io.File
import kotlin.concurrent.thread


class InstagramPlanerPresenter(val context: Context) : BaseMvpPresenterImpl<InstagramPlanerView>() {

    companion object {
        private const val DIR_ROOT = "instagram_planer"
    }



    val images = mutableListOf<Uri>()
    val users = mutableListOf<User>()
    var imagePressPosition: Int = -1

    var folders = mutableListOf<String>()
    var currentUser: User? = null
        set(value) {
            if(value != null){
                field = value
                view?.setWallName(value = "@${value.name}")
            }
        }

    val userFolder: String
        get() = "$DIR_ROOT/${currentUser?.name}"

//    private suspend fun loadImage() = withContext(Dispatchers.IO) {
//        transferObject.images.reversed().forEachIndexed { index, bitmap ->
//            context.saveBitmap(bitmap, "${index}_")
//        }
//    }

    private fun checkVisibleView() {
        view?.setVisiblePlaceholder(isVisible = this.images.isEmpty())
        view?.setVisibleClearAll(isVisible = this.images.isNotEmpty())
        view?.setVisibleRemoveAcc(isVisible = folders.size > 1)
        view?.setVisibleAccountMoreIcon(isVisible = folders.size > 1)
        view?.updateBottomMenu()
    }

    fun loadUsers() = CoroutineScope(Main).launch {
        withContext(Dispatchers.IO) {
            users.clear()
//            users.addAll(userDao?.getAll() ?: listOf())
        }
    }

    private fun createUser(name: String) = CoroutineScope(Main).launch {
                val newUser = withContext(Dispatchers.IO) {
                    User().apply {
                        this.id = 1
                        this.name = name
//                        userDao?.insert(this)
                    }
                }
//                currentUser = newUser
    }


    fun onLoad() {
        this.images.clear()
//        folders = FileStorage.readFilesNameFromDir(dirName = DIR_ROOT)
        if (users.isEmpty()) {
            view?.createDialogInputName()
        } else {
            currentUser = users.firstOrNull()
            val images = FileStorage.readFilesFromDir(dirName = userFolder)
            this.images.addAll(images)
        }
        view?.updateList()
        checkVisibleView()
    }

    fun onInputName(value: String?) {
         val db = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "instagram_planer_database").build()
         val userDao = db.userDao()
        when {
            !value.isNullOrEmpty() && value.isNotBlank() -> {
                thread {
                    User().apply {
                        this.id = 1
                        this.name = value
                        userDao?.insert(this)
                    }
                }
//                createUser(name = value.trim())
//                FileStorage.makeDir(dirName = userFolder)
//                folders.add(currentFolder)
//                this.images.clear()
//                view?.updateList()
//                checkVisibleView()
            }
            folders.isEmpty() -> {
                view?.backPress()
            }
        }
    }

    fun pressListAccount(position: Int) {
        users.getOrNull(position)?.let { newUser ->
            if (currentUser?.id != newUser.id) {
                currentUser = newUser
                this.images.clear()
                val images = FileStorage.readFilesFromDir(dirName = userFolder)
                this.images.addAll(images)
                view?.updateList()
                checkVisibleView()
            }
        }
    }

    fun onAccountClearConfirm(value: Boolean) {
        if (value) {
            this.images.clear()
            view?.updateList()
            checkVisibleView()
            FileStorage.clearDir(dirName = userFolder)
        }
    }

    fun deleteImage() {
        this.images.removeAt(imagePressPosition).let {
            FileStorage.removeFile(file = File(getPath(it)))
        }
    }

    fun changeImage(uri: Uri) {
        this.images.getOrNull(imagePressPosition)?.toFile()?.let {
            File(getPath(uri)).copyTo(target = it, overwrite = true)
        }
    }

    fun onRemoveAccount(value: Boolean) {
        if (value) {
//            FileStorage.removeFile(fileName = currentFolder, dirName = DIR_ROOT)
//            onLoad()
        }
    }

    fun pressMoreAccount() {
        view?.createDialogList(list = folders, select = currentUser?.name ?: "")
    }

    fun pressImage(position: Int) {
        imagePressPosition = position
        view?.goToImage(uri = this.images[position])
    }

    fun onChangeImagePosition(oldPosition: Int, newPosition: Int) {
        val oldImage = this.images[oldPosition]
        val newImage = this.images[newPosition]
        this.images[oldPosition] = newImage
        this.images[newPosition] = oldImage
        view?.changeImageInList(position = oldPosition)
        view?.changeImageInList(position = newPosition)
    }

    fun onAddImages(imageUris: List<Uri>) {
        imageUris.forEach {
            images.add(0, it)
            FileStorage.copyFileToDir(file = File(getPath(it)), dirName = userFolder, fileName = images.size.toString())
        }
        view?.addImageToList(count = imageUris.size)
        checkVisibleView()
    }

    fun getPath(uri: Uri): String {
        val projection = arrayOf<String>(MediaStore.Images.Media.DATA)
        val cursor: Cursor = context.getContentResolver().query(uri, projection, null, null, null)
                ?: return ""
        val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val s: String = cursor.getString(column_index)
        cursor.close()
        return s
    }
}
