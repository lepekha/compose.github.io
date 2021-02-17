package com.inhelp.text_style.main

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.inhelp.base.mvp.BaseMvpFragment
import com.inhelp.extension.onTextChangedListener
import com.inhelp.text_style.R
import kotlinx.android.synthetic.main.fragment_text_style.*
import kotlinx.android.synthetic.main.fragment_text_style.view.*
import org.koin.android.ext.android.inject


class TextStyleFragment : BaseMvpFragment<TextStyleView, TextStylePresenter>(), TextStyleView {

    companion object {
        fun newInstance(): TextStyleFragment {
            return TextStyleFragment()
        }
    }

    override val presenter: TextStylePresenter by inject()

    private fun initList() {
        list.layoutManager = LinearLayoutManager(getCurrentContext(), RecyclerView.VERTICAL, false)
        list.adapter = MenuRvAdapter(presenter.stringList)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_text_style, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getCurrentContext().getString(R.string.fragment_title_text_style))
        initList()
        editText.onTextChangedListener {
            presenter.enterText(it)
        }
        btnClear.setOnClickListener {
            editText.text.clear()
            presenter.enterText("")
        }
    }

    override fun setVisibleClearText(isVisible: Boolean) {
        btnClear.isVisible = isVisible
    }

    override fun setVisiblePlaceholder(isVisible: Boolean) {
        imgPlaceholder.isVisible = isVisible
    }

    override fun setEnterText(value: String) {
        editText.setText(value)
    }

    override fun updateListAll() {
        list.adapter?.notifyDataSetChanged()
    }
}