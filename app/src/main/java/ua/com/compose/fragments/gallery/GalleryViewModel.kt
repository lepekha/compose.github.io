package ua.com.compose.fragments.gallery

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GalleryViewModel(val context: Context) : ViewModel() {

    sealed class State {
        object NONE: State()
        object INIT_IMAGES: State()
        object INIT_FOLDERS: State()
        object CLEAR_SELECT: State()
        object BACK_PRESS: State()
        data class FOLDER_NAME(val name: String): State()
        data class VISIBLE_BUTTON(val isVisible: Boolean): State()
    }

    private val _images: MutableLiveData<List<Uri>> = MutableLiveData(listOf())
    val images: LiveData<List<Uri>> = _images

    private val _folders: MutableLiveData<List<ImageFolder>> = MutableLiveData(listOf())
    val folders: LiveData<List<ImageFolder>> = _folders

    private val _state: MutableLiveData<GalleryViewModel.State> = MutableLiveData(GalleryViewModel.State.NONE)
    val state: LiveData<GalleryViewModel.State> = _state

    var folderForImages = mutableListOf<ImageFolder>()
    val selectedImages = mutableListOf<Uri>()
    var doneImages = mutableListOf<Uri>()
    private var _multiSelect = false
    var isMultiSelect: Boolean = false
        set(value) {
            field = value && _multiSelect
            _state.value = State.VISIBLE_BUTTON(field)
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
        _state.value = State.FOLDER_NAME(name = value.name)
        _state.value = State.INIT_IMAGES
        _images.postValue(value.images)
    }

    fun getAllShownImagesPath(activity: Activity)= CoroutineScope(Dispatchers.IO).launch {
        val uriExternal: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val allDirName = "All"
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


        folderForImages.clear()
        folderForImages.addAll(imageFolders.values)
        _images.postValue(folderForImages[0].images)
    }

    fun pressFolderName() {
        _state.value = State.INIT_FOLDERS
        _folders.postValue(folderForImages)
    }

    fun pressClear(){
        selectedImages.clear()
        isMultiSelect = false
        _state.value = State.CLEAR_SELECT
    }

    fun addImage(){
        doneImages = selectedImages
        _state.value = State.BACK_PRESS
    }
}

class ImageFolder {
    var name: String = ""
    val images: MutableList<Uri> = mutableListOf()
}