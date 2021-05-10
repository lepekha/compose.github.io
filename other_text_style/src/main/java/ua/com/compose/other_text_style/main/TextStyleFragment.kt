package ua.com.compose.other_text_style.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.module_other_text_style_fragment_text_style.*
import ua.com.compose.mvp.BaseMvpFragment
import org.koin.android.ext.android.inject
import ua.com.compose.extension.onTextChangedListener
import ua.com.compose.other_text_style.R


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
        return inflater.inflate(R.layout.module_other_text_style_fragment_text_style, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getCurrentContext().getString(R.string.module_other_text_style_fragment_title_text_style))
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