package com.dali.instagram.planer.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.dali.instagram.planer.R
import com.dali.instagram.planer.di.Scope
import com.inhelp.base.mvp.BaseMvpFragment
import com.inhelp.extension.createInstagramIntent
import com.inhelp.gallery.main.FragmentGallery
import data.BottomMenu
import data.Menu
import kotlinx.android.synthetic.main.fragment_instagram_planer.*
import replace


class InstagramPlanerFragment : BaseMvpFragment<InstagramPlanerView, InstagramPlanerPresenter>(), InstagramPlanerView {

    companion object {
        fun newInstance(): InstagramPlanerFragment {
            return InstagramPlanerFragment()
        }
    }

    override val presenter: InstagramPlanerPresenter by lazy {
        Scope.INSTAGRAM.get()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_instagram_planer, container, false)
    }

    override fun createBottomMenu(): MutableList<Menu> {

        val btnGallery = BottomMenu(iconResId = com.inhelp.theme.R.drawable.ic_gallery){
            getCurrentActivity().supportFragmentManager.replace(fragment = FragmentGallery.newInstance(targetFragment = this), addToBackStack = true)
        }

        return mutableListOf<Menu>().apply {
            this.add(btnGallery)
        }
    }

    private fun initGridPreview() {
        gridList.layoutManager = GridLayoutManager(getCurrentContext(), 3)
        gridList.adapter = InstagramPlanerRvAdapter(
                images = presenter.images,
                onPress = {
//                    presenter.pressImage(it)
                })
    }

    override fun updateList(){
        gridList.adapter?.notifyDataSetChanged()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getCurrentContext().getString(R.string.fragment_instagram_grid_title))
        presenter.onLoad(uriString = arguments?.getString(FragmentGallery.ARGUMENT_ONE_URI))
        initGridPreview()
    }

//    override fun createInstagramIntent(uri: Uri) {
//        getCurrentActivity().createInstagramIntent(uri)
//    }

    override fun backPress(): Boolean {
        backToMain()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        Scope.INSTAGRAM.close()
    }
}