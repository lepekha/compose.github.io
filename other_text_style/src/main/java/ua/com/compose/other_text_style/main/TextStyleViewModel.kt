package ua.com.compose.other_text_style.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.com.compose.other_text_style.domain.GetStringsUseCase
import ua.com.compose.other_text_style.utils.TextStyleConverter
import ua.com.compose.other_text_style.utils.TranslConverter

class TextStyleViewModel(private val getStringsUseCase: GetStringsUseCase): ViewModel() {

    private val _isVisibleClearText: MutableLiveData<Boolean> = MutableLiveData(false)
    val isVisibleClearText: LiveData<Boolean> = _isVisibleClearText

    private val _isVisiblePlaceHolder: MutableLiveData<Boolean> = MutableLiveData(false)
    val isVisiblePlaceHolder: LiveData<Boolean> = _isVisiblePlaceHolder

    private val _strings: MutableLiveData<List<String>> = MutableLiveData(mutableListOf())
    val strings: LiveData<List<String>> = _strings

    init {
        enterText("")
    }

    fun enterText(value: String){
        viewModelScope.launch {
            var inputText = value
            if(value.isEmpty() or value.isBlank()){
                inputText = "Enter text"
            }
            val list = getStringsUseCase.execute(inputText = inputText)
            _isVisibleClearText.postValue(value.isNotEmpty())
            _isVisiblePlaceHolder.postValue(list.isEmpty())
            _strings.postValue(list)
        }
    }

}