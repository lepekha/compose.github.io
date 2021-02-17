package com.inhelp.text_style.main

import com.inhelp.base.mvp.BaseMvpPresenterImpl
import com.inhelp.text_style.utils.TextStyleConverter
import com.inhelp.text_style.utils.TranslConverter


class TextStylePresenter: BaseMvpPresenterImpl<TextStyleView>() {

    private val converter = TextStyleConverter.createConverter()

    val stringList = mutableListOf<String>()

    override fun attachView(view: TextStyleView) {
        super.attachView(view)
        val text = "Enter text"
        enterText(text)
    }

    fun enterText(value: String){
        stringList.clear()
        var inputText = value
        if(value.isEmpty() or value.isBlank()){
            inputText = "Enter text"
        }
        (0 until converter.size()).forEach {
            stringList.add(converter.convertString(string = TranslConverter.getCorrectString(oldString = inputText), pack = it))
        }
        view?.setVisibleClearText(isVisible = value.isNotEmpty())
        view?.setVisiblePlaceholder(isVisible = stringList.isEmpty())
        view?.updateListAll()
    }
}