package com.inhelp.view.main.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.inhelp.R
import kotlinx.android.synthetic.main.fragment_main.*
import org.koin.android.ext.android.inject
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.inhelp.base.mvp.BaseMvpFragment
import org.koin.android.ext.android.getKoin
import org.koin.core.qualifier.named


class FragmentMain : BaseMvpFragment<ViewMain, PresenterMain>(), ViewMain {

    override val presenter: PresenterMain by lazy {
        val scope = getKoin().getOrCreateScope(
                "app", named("app"))
        scope.get()
    }

    private lateinit var mMenuRvAdapter: MenuRvAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getCurrentContext().getString(R.string.fragment_title_menu))
        setVisibleBack(false)
        initList()
        presenter.updateList()
    }

    private fun initList() {
        menuList.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        mMenuRvAdapter = MenuRvAdapter(presenter.getOrCreateMenu(fm = fragmentManager))
        menuList?.adapter = mMenuRvAdapter
    }

    override fun updatePhotoList(){
    }
}