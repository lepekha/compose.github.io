package com.inhelp.tags.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.inhelp.base.mvp.BaseMvpFragment
import com.inhelp.extension.onTextChangedListener
import com.inhelp.tags.R
import kotlinx.android.synthetic.main.fragment_tags.*
import org.koin.android.ext.android.inject


class FragmentTags : BaseMvpFragment<ViewTags, PresenterTags>(), ViewTags {

    companion object {
        fun newInstance(): FragmentTags {
            return FragmentTags()
        }
    }

    override val presenter: PresenterTags by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tags, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getCurrentContext().getString(R.string.fragment_title_tags))
        initList()
        editText.onTextChangedListener {
            presenter.onEnterText(it)
        }
        btnClear.setOnClickListener {
            editText.text.clear()
            presenter.onEnterText("")
        }
    }

    private fun initList(){
        list.layoutManager = LinearLayoutManager(getCurrentContext(), RecyclerView.VERTICAL, false)
        list.adapter = TagsRvAdapter(presenter.tags, presenter.filterText)
    }

    override fun updateAllList(){
        list.adapter?.notifyDataSetChanged()
    }

    override fun setVisibleClearText(isVisible: Boolean) {
        btnClear.isVisible = isVisible
    }

    override fun setVisiblePlaceholder(isVisible: Boolean) {
        imgPlaceholder.isVisible = isVisible
    }

}