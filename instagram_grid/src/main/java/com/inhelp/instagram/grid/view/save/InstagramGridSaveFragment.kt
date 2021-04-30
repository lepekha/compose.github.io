package com.inhelp.instagram.grid.view.save

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.inhelp.base.mvp.BaseMvpFragment
import com.inhelp.extension.createInstagramIntent
import com.inhelp.instagram.R
import com.inhelp.instagram.grid.di.Scope
import data.BottomMenu
import data.Menu
import kotlinx.android.synthetic.main.fragment_instagram_grid_save.*


class InstagramGridSaveFragment : BaseMvpFragment<InstagramGridSaveView, InstagramGridSavePresenter>(), InstagramGridSaveView {

    companion object {
        fun newInstance(): InstagramGridSaveFragment {
            return InstagramGridSaveFragment()
        }
    }

    override val presenter: InstagramGridSavePresenter by lazy {
        Scope.INSTAGRAM.get()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_instagram_grid_save, container, false)
    }

    private val btnDownload by lazy {
        BottomMenu(iconResId = com.inhelp.theme.R.drawable.ic_download) {
            presenter.pressSave()
        }
    }

    override fun createBottomMenu(): MutableList<Menu> {
        return mutableListOf<Menu>().apply {
            this.add(btnDownload)
        }
    }

    private fun initGridPreview() {
        gridList.layoutManager = GridLayoutManager(getCurrentContext(), 3)
        gridList.adapter = GridRvAdapter(
                images = presenter.gridImages,
                onPress = {
                    presenter.pressImage(it)
                })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getCurrentContext().getString(R.string.module_instagram_grid_save_images))
        presenter.onCreate()
        initGridPreview()
    }

    override fun createInstagramIntent(uri: Uri) {
        getCurrentActivity().createInstagramIntent(uri)
    }

    override fun backPress(): Boolean {
        backToMain()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        Scope.INSTAGRAM.close()
    }
}