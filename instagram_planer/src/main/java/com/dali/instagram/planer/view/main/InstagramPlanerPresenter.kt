package com.dali.instagram.planer.view.main

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.core.net.toFile
import com.dali.file.FileStorage
import com.inhelp.base.mvp.BaseMvpPresenterImpl
import java.io.File


class InstagramPlanerPresenter(val context: Context): BaseMvpPresenterImpl<InstagramPlanerView>() {

    companion object {
        private const val DIR_ROOT = "instagram_planer"
    }

    val images = mutableListOf<Uri>()
    var imagePressPosition: Int = -1

    var folders = mutableListOf<String>()
    var currentFolder = ""
        set(value) {
            field = value
            view?.setWallName(value = "@$field")
        }
    
    val userFolder: String
        get() = "$DIR_ROOT/$currentFolder"

//    private suspend fun loadImage() = withContext(Dispatchers.IO) {
//        transferObject.images.reversed().forEachIndexed { index, bitmap ->
//            context.saveBitmap(bitmap, "${index}_")
//        }
//    }

    private fun checkVisibleView(){
        view?.setVisiblePlaceholder(isVisible = this.images.isEmpty())
        view?.setVisibleClearAll(isVisible = this.images.isNotEmpty())
        view?.setVisibleRemoveAcc(isVisible = folders.size > 1)
        view?.setVisibleAccountMoreIcon(isVisible = folders.size > 1)
        view?.updateBottomMenu()
    }

    fun onLoad(){
        this.images.clear()
        folders = FileStorage.readFilesNameFromDir(dirName = DIR_ROOT)
        if(folders.isEmpty()){
            view?.createDialogInputName()
        }else{
            currentFolder = folders.first()
            val images = FileStorage.readFilesFromDir(dirName = userFolder)
            this.images.addAll(images)
        }
        view?.updateList()
        checkVisibleView()
    }

    fun onInputName(value: String?){
        when{
            !value.isNullOrEmpty() && value.isNotBlank() -> {
                currentFolder = value.trim()
                FileStorage.makeDir(dirName = userFolder)
                folders.add(currentFolder)
                this.images.clear()
                view?.updateList()
                checkVisibleView()
            }
            folders.isEmpty() -> {
                view?.backPress()
            }
        }
    }

    fun pressListAccount(position: Int){
        folders.getOrNull(position)?.let { newFolder ->
            if(currentFolder != newFolder){
                currentFolder = newFolder
                this.images.clear()
                val images = FileStorage.readFilesFromDir(dirName = userFolder)
                this.images.addAll(images)
                view?.updateList()
                checkVisibleView()
            }
        }
    }

    fun onAccountClearConfirm(value: Boolean){
        if(value){
            this.images.clear()
            view?.updateList()
            checkVisibleView()
            FileStorage.clearDir(dirName = userFolder)
        }
    }

    fun deleteImage(){
        this.images.removeAt(imagePressPosition).let {
            FileStorage.removeFile(file = File(getPath(it)))
        }
    }

    fun changeImage(uri: Uri){
        this.images.getOrNull(imagePressPosition)?.toFile()?.let {
            File(getPath(uri)).copyTo(target = it, overwrite = true)
        }
    }

    fun onRemoveAccount(value: Boolean){
        if(value){
            FileStorage.removeFile(fileName = currentFolder, dirName = DIR_ROOT)
            onLoad()
        }
    }

    fun pressMoreAccount(){
        view?.createDialogList(list = folders, select = currentFolder)
    }

    fun pressImage(position: Int){
        imagePressPosition = position
        view?.goToImage(uri = this.images[position])
    }

    fun onChangeImagePosition(oldPosition: Int, newPosition: Int){
        val oldImage = this.images[oldPosition]
        val newImage = this.images[newPosition]
        this.images[oldPosition] = newImage
        this.images[newPosition] = oldImage
        view?.changeImageInList(position = oldPosition)
        view?.changeImageInList(position = newPosition)
    }

    fun onAddImages(imageUris: List<Uri>){
        imageUris.forEach {
            images.add(0, it)
            FileStorage.copyFileToDir(file = File(getPath(it)), dirName = userFolder, fileName = images.size.toString())
        }
        view?.addImageToList(count = imageUris.size)
        checkVisibleView()
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

}