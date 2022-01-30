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

    fun enterText(value: String){
        viewModelScope.launch {
            var inputText = value
            val list = getStringsUseCase.execute(inputText = inputText)
            _isVisiblePlaceHolder.postValue(list.isEmpty())
            _strings.postValue(list)
        }
    }

}