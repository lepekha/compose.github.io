package ua.com.compose.instagram_planer.view.main

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.com.compose.dialog.DialogManager
import ua.com.compose.extension.saveBitmap
import ua.com.compose.instagram_planer.R
import ua.com.compose.instagram_planer.data.Image
import ua.com.compose.instagram_planer.data.User
import ua.com.compose.instagram_planer.view.domain.*

class InstagramPlanerViewModel(private val createUserUseCase: CreateUserUseCase,
                               private val getAllUsersUseCase: GetAllUsersUseCase,
                               private val getAllUserImagesUseCase: GetAllUserImagesUseCase,
                               private val removeUserUseCase: RemoveUserUseCase,
                               private val imageUpdateUseCase: ImageUpdateUseCase,
                               private val addImagesForUserUseCase: AddImagesForUserUseCase,
                               private val removeAllImagesFromUserUseCase: RemoveAllImagesFromUserUseCase,
                               private val imageChangePositionsUseCase: ImageChangePositionsUseCase,
                               private val imageRemoveUseCase: ImageRemoveUseCase,
                               private val imageChangeUseCase: ImageChangeUriUseCase,
                               private val imageDownloadUseCase: ImageDownloadUseCase
): ViewModel() {

    private val _isVisiblePlaceHolder: MutableLiveData<Boolean> = MutableLiveData(true)
    val isVisiblePlaceHolder: LiveData<Boolean> = _isVisiblePlaceHolder

    private val _isVisibleClearAll: MutableLiveData<Boolean> = MutableLiveData(false)
    val isVisibleClearAll: LiveData<Boolean> = _isVisibleClearAll

    private val _saveImage: MutableLiveData<Bitmap?> = MutableLiveData(null)
    val saveImage: LiveData<Bitmap?> = _saveImage

    private val _isVisibleMore: MutableLiveData<Boolean> = MutableLiveData(true)
    val isVisibleMore: LiveData<Boolean> = _isVisibleMore

    private val _changeImageInList: MutableLiveData<Int> = MutableLiveData(0)
    val changeImageInList: LiveData<Int> = _changeImageInList

    private val _goToImage: MutableLiveData<Image?> = MutableLiveData(null)
    val goToImage: LiveData<Image?> = _goToImage

    private val _userName: MutableLiveData<String> = MutableLiveData("")
    val userName: LiveData<String> = _userName

    private val _createDialogInputName: MutableLiveData<String?> = MutableLiveData(null)
    val createDialogInputName: LiveData<String?> = _createDialogInputName

    private val _createUsersList: MutableLiveData<List<String>> = MutableLiveData(listOf())
    val createUsersList: LiveData<List<String>> = _createUsersList

    private val _createAlert: MutableLiveData<Int?> = MutableLiveData(null)
    val createAlert: LiveData<Int?> = _createAlert

    private val _images: MutableLiveData<List<Image>> = MutableLiveData(mutableListOf())
    val images: LiveData<List<Image>> = _images

    private val _removeImage: MutableLiveData<Image?> = MutableLiveData(null)
    val removeImage: LiveData<Image?> = _removeImage

    private val _changeImages: MutableLiveData<List<Image>> = MutableLiveData(mutableListOf())
    val changeImages: LiveData<List<Image>> = _changeImages

    var currentImage: Image? = null

    var currentUser: User? = null
        set(value) {
            if(value != null){
                field = value
                _userName.postValue("@${value.name}")
            }else{
                _userName.postValue("")
            }
        }

    fun loadUsers() = viewModelScope.launch {
        val users = getAllUsersUseCase.execute()
        currentUser = users.sortedBy { it.name }.firstOrNull { it.currentUser }
        val images = currentUser?.let { getAllUserImagesUseCase.execute(user = it) } ?: listOf()
        _images.postValue(images)
        _createUsersList.postValue(users.filter { it != currentUser }.map { it.name })
        _isVisiblePlaceHolder.postValue(images.isEmpty())
        _isVisibleClearAll.postValue(images.isNotEmpty())
        _isVisibleMore.postValue(users.isNotEmpty())
    }

    private fun createUser(name: String) = viewModelScope.launch {
        val users = getAllUsersUseCase.execute()

        if(users.any { it.name == name }){
            _createAlert.postValue(R.string.module_instagram_palaner_user_already_contain)
            _createDialogInputName.postValue(name)
            return@launch
        }

        currentUser = createUserUseCase.execute(userName = name)
        _images.postValue(listOf())
        _createUsersList.postValue(users.filter { it != currentUser }.map { it.name })
        _isVisiblePlaceHolder.postValue(true)
        _isVisibleClearAll.postValue(false)
        _isVisibleMore.postValue(true)
    }

    fun onAccountChange(name: String?) = viewModelScope.launch {
        val users = getAllUsersUseCase.execute()
        currentUser = users.firstOrNull { it.name == name }?.apply {
            val images = getAllUserImagesUseCase.execute(user = this)
            _images.postValue(images)
            _createUsersList.postValue(users.filter { it != this }.map { it.name })
            _isVisiblePlaceHolder.postValue(images.isEmpty())
            _isVisibleClearAll.postValue(images.isNotEmpty())
        }
    }

    fun onRemoveAccount(value: Boolean) = viewModelScope.launch {
        if(value) {
            currentUser?.let { user ->
                removeUserUseCase.execute(user)
            }
            loadUsers()
        }
    }

    fun pressImageSave(position: Int) = viewModelScope.launch {
        currentUser?.let { user ->
            val dialog = DialogManager.createLoad{}
            val images = getAllUserImagesUseCase.execute(user = user)
            imageDownloadUseCase.execute(images[position])
            _createAlert.postValue(R.string.module_instagram_palaner_save_ready)
            dialog.closeDialog()
        }
    }

    fun pressImage(position: Int) = viewModelScope.launch {
        currentUser?.let { user ->
            val images = getAllUserImagesUseCase.execute(user)
            currentImage = images[position]
            _goToImage.postValue(currentImage)
        }
    }

    fun onChangeImagePosition(oldPosition: Int, newPosition: Int) = viewModelScope.launch {
        currentUser?.let { user ->
            imageChangePositionsUseCase.execute(user, oldPosition, newPosition)
            getAllUserImagesUseCase.execute(user).let {
                _changeImages.postValue(it)
            }
            _changeImageInList.value = oldPosition
            _changeImageInList.value = newPosition
        }
    }

    fun onAddImages(imageUris: List<Uri>) = viewModelScope.launch {
        currentUser?.let { user ->
            addImagesForUserUseCase.execute(user = user, uris = imageUris)
            val images = getAllUserImagesUseCase.execute(user)
            _images.postValue(images)
            _isVisiblePlaceHolder.postValue(images.isEmpty())
            _isVisibleClearAll.postValue(images.isNotEmpty())
        }
    }

    private var imageForChange: Image? = null
    fun onChangeImage(position: Int) = viewModelScope.launch {
        currentUser?.let { user ->
            val images = getAllUserImagesUseCase.execute(user = user)
            imageForChange = images[position]
        }
    }

    fun pressChangeImage(imageUris: List<Uri>) = viewModelScope.launch {
        currentUser?.let { user ->
            imageForChange?.let { image ->
                imageChangeUseCase.execute(user = user, image = image, uri = imageUris.first())
                val images = getAllUserImagesUseCase.execute(user)
                _changeImages.value = images
                _changeImageInList.value = images.indexOfFirst { it.id == imageForChange?.id }
                imageForChange = null
            }
        }
    }

    fun onInputName(value: String?) {
        when {
            !value.isNullOrEmpty() && value.isNotBlank() -> {
                createUser(name = value.trim())
            }
        }
    }

    fun onAccountClearConfirm(value: Boolean) = viewModelScope.launch {
        if (value) {
            currentUser?.let { user ->
                removeAllImagesFromUserUseCase.execute(user = user)
                val images = getAllUserImagesUseCase.execute(user)
                _images.postValue(images)
                _isVisiblePlaceHolder.postValue(images.isEmpty())
                _isVisibleClearAll.postValue(images.isNotEmpty())
            }
        }
    }

    fun pressAddUser(){
        _createDialogInputName.postValue("")
    }

    fun pressRemoveImage(position: Int) = viewModelScope.launch {
        currentUser?.let { user ->
            val images = getAllUserImagesUseCase.execute(user).toMutableList()
            imageRemoveUseCase.execute(images[position])
            _removeImage.value = images[position]
        }
    }
}