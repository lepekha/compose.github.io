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
import ua.com.compose.mvp.data.SingleLiveEvent

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
                               private val imageDownloadUseCase: ImageDownloadUseCase,
                               private val userChangeUseCase: UserChangeUseCase
): ViewModel() {

    private val _isVisiblePlaceHolder: MutableLiveData<Boolean> = MutableLiveData(true)
    val isVisiblePlaceHolder: LiveData<Boolean> = _isVisiblePlaceHolder

    private val _isVisibleClearAll: MutableLiveData<Boolean> = MutableLiveData(false)
    val isVisibleClearAll: LiveData<Boolean> = _isVisibleClearAll

    private val _isVisibleMore: MutableLiveData<Boolean> = MutableLiveData(true)
    val isVisibleMore: LiveData<Boolean> = _isVisibleMore

    private val _changeImageInList = SingleLiveEvent<Int>()
    val changeImageInList: LiveData<Int> = _changeImageInList

    private val _goToImage: SingleLiveEvent<Image?> = SingleLiveEvent()
    val goToImage: LiveData<Image?> = _goToImage

    private val _userName: SingleLiveEvent<String> = SingleLiveEvent()
    val userName: LiveData<String> = _userName

    private val _createDialogInputName: SingleLiveEvent<String?> = SingleLiveEvent()
    val createDialogInputName: LiveData<String?> = _createDialogInputName

    private val _createUsersList: SingleLiveEvent<List<String>> = SingleLiveEvent()
    val createUsersList: LiveData<List<String>> = _createUsersList

    private val _createAlert: SingleLiveEvent<Int?> = SingleLiveEvent()
    val createAlert: LiveData<Int?> = _createAlert

    private val _images: SingleLiveEvent<List<Image>> = SingleLiveEvent()
    val images: LiveData<List<Image>> = _images

    private val _removeImage: SingleLiveEvent<Image?> = SingleLiveEvent()
    val removeImage: LiveData<Image?> = _removeImage

    private val _addImages: SingleLiveEvent<List<Image>> = SingleLiveEvent()
    val addImages: LiveData<List<Image>> = _addImages

    private val _changeImages: SingleLiveEvent<List<Image>> = SingleLiveEvent()
    val changeImages: LiveData<List<Image>> = _changeImages

    private var currentImage: Image? = null

    private var currentUser: User? = null
        set(value) {
            if(value != null){
                _userName.postValue("@${value.name}")
            }else{
                _userName.postValue("")
            }
            field = value
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
            userChangeUseCase.execute(this)
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
            _goToImage.value = currentImage
            _goToImage.value = null
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
            val load = DialogManager.createLoad{}
            imageUris.forEach {
                addImagesForUserUseCase.execute(user = user, uri = it)
                val images = getAllUserImagesUseCase.execute(user)
                _addImages.postValue(images)
                _isVisiblePlaceHolder.postValue(images.isEmpty())
                _isVisibleClearAll.postValue(images.isNotEmpty())
            }
            load.closeDialog()
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
                val load = DialogManager.createLoad{}
                imageUris.firstOrNull()?.let { uri ->
                    imageChangeUseCase.execute(user = user, image = image, uri = uri)
                    val images = getAllUserImagesUseCase.execute(user)
                    _changeImages.value = images
                    _changeImageInList.value = images.indexOfFirst { it.id == imageForChange?.id }
                    imageForChange = null
                }
                load.closeDialog()
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
            imageRemoveUseCase.execute(user, images[position])
            _isVisibleClearAll.postValue((images.size - 1) > 0)
            _removeImage.value = images[position]
        }
    }

    fun pressAddBox(color: Int) = viewModelScope.launch {
        currentUser?.let { user ->
            val bitmap = Bitmap.createBitmap(1024, 1024, Bitmap.Config.ARGB_8888).apply {
                this.eraseColor(color)
            }
            addImagesForUserUseCase.execute(user, bitmap)
            val images = getAllUserImagesUseCase.execute(user)
            _images.postValue(images)
            _isVisiblePlaceHolder.postValue(images.isEmpty())
            _isVisibleClearAll.postValue(images.isNotEmpty())
        }
    }
}