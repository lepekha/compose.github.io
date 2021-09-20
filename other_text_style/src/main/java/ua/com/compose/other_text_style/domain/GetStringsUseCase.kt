package ua.com.compose.other_text_style.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.com.compose.other_text_style.utils.Converter
import ua.com.compose.other_text_style.utils.TranslConverter

class GetStringsUseCase(private val converter: Converter) {

    suspend fun execute(inputText: String): List<String> {
        return withContext(Dispatchers.IO){
            val list = mutableListOf<String>()
            (0 until converter.size()).forEach {
                list.add(converter.convertString(string = TranslConverter.getCorrectString(oldString = inputText), pack = it))
            }
            list
        }
    }

}