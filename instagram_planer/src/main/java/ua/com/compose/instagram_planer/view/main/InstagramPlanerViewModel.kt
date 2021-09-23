package ua.com.compose.instagram_planer.view.main

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.com.compose.file_storage.FileStorage
import ua.com.compose.instagram_planer.R
import ua.com.compose.instagram_planer.data.Image
import ua.com.compose.instagram_planer.data.User
import ua.com.compose.instagram_planer.view.domain.*
import java.io.File

class InstagramPlanerViewModel(private val createUserUseCase: CreateUserUseCase,
                               private val getAllUsersUseCase: GetAllUsersUseCase,
                               private val getAllUserImagesUseCase: GetAllUserImagesUseCase,
                               private val removeUserUseCase: RemoveUserUseCase,
                               private val updateImageUseCase: UpdateImageUseCase,
                               private val addImagesForUserUseCase: AddImagesForUserUseCase,
                               private val removeAllImagesFromUserUseCase: RemoveAllImagesFromUserUseCase,
                               private val changeImagePositionsUseCase: ChangeImagePositionsUseCase
): ViewModel() {

    private val _isVisiblePlaceHolder: MutableLiveData<Boolean> = MutableLiveData(false)
    val isVisiblePlaceHolder: LiveData<Boolean> = _isVisiblePlaceHolder

    private val _isVisibleClearAll: MutableLiveData<Boolean> = MutableLiveData(false)
    val isVisibleClearAll: LiveData<Boolean> = _isVisibleClearAll

    private val _isVisibleRemoveAccount: MutableLiveData<Boolean> = MutableLiveData(false)
    val isVisibleRemoveAccount: LiveData<Boolean> = _isVisibleRemoveAccount

    private val _isVisibleMoreAccount: MutableLiveData<Boolean> = MutableLiveData(false)
    val isVisibleMoreAccount: LiveData<Boolean> = _isVisibleMoreAccount

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

    private val _createAlert: MutableLiveData<Int> = MutableLiveData(-1)
    val createAlert: LiveData<Int> = _createAlert

    private val _images: MutableLiveData<List<Image>> = MutableLiveData(mutableListOf())
    val images: LiveData<List<Image>> = _images



    var currentImage: Image? = null

    var currentUser: User? = null
        set(value) {
            if(value != null){
                field = value
                _userName.postValue("@${value.name}")
            }
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
        _isVisibleClearAll.postValue(true)
        _isVisibleRemoveAccount.postValue((users.size + 1) > 1)
    }

    fun loadUsers() = viewModelScope.launch {
        val users = getAllUsersUseCase.execute()

        if (users.isEmpty()) {
            _createDialogInputName.postValue(null)
        } else {
            currentUser = users.sortedBy { it.name }.first { it.currentUser }.apply {
                val images = getAllUserImagesUseCase.execute(user = this)
                _images.postValue(images)
                _createUsersList.postValue(users.filter { it != this }.map { it.name })
                _isVisiblePlaceHolder.postValue(images.isEmpty())
                _isVisibleClearAll.postValue(images.isNotEmpty())
                _isVisibleRemoveAccount.postValue(images.size > 1)
            }
        }
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

    fun pressMoreAccount() = viewModelScope.launch {
        val users = getAllUsersUseCase.execute()
//        view?.createDialogList(list = users.map { it.name }.sortedBy { it }, select = currentUser?.name ?: "")
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
            changeImagePositionsUseCase.execute(user, oldPosition, newPosition)
            _changeImageInList.postValue(oldPosition)
            _changeImageInList.postValue(newPosition)
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

//    fun deleteImage() = CoroutineScope(Dispatchers.Main).launch {
//        withContext(Dispatchers.IO) {
//            imageDao?.delete(image = images[imagePressPosition])
//        }
//    }
}