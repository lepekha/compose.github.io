package com.dali.instagram.planer.view.main

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.GridLayoutManager
import com.dali.instagram.planer.R
import com.dali.instagram.planer.di.Scope
import com.inhelp.base.mvp.BaseMvpFragment
import com.inhelp.dialogs.main.input.DialogInput
import com.inhelp.extension.createInstagramIntent
import com.inhelp.extension.scrollTo
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

        val btnGallery = BottomMenu(iconResId = com.inhelp.theme.R.drawable.ic_gallery) {
            FragmentGallery.show(fm = getCurrentActivity().supportFragmentManager, isMultiSelect = true)
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
                },
                onChange = { oldPosition, newPosition ->
                    presenter.onChangeImagePosition(oldPosition, newPosition)
                })
    }

    override fun updateList() {
        gridList.adapter?.notifyDataSetChanged()
    }

    override fun changeImageInList(position: Int) {
        gridList.adapter?.notifyItemChanged(position, InstagramPlanerRvAdapter.CHANGE_ITEM_POSITION)
    }

    override fun addImageToList(count: Int){
        gridList.adapter?.notifyItemRangeInserted(0, count)
        gridList.scrollTo(0)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getCurrentContext().getString(R.string.fragment_instagram_grid_title))
        setFragmentResultListener(FragmentGallery.REQUEST_KEY) { _, bundle ->
            presenter.onAddImages((bundle.getSerializable(FragmentGallery.BUNDLE_KEY_IMAGES) as List<*>).filterIsInstance<Uri>())
        }
        initGridPreview()

        btnAccAdd.setOnClickListener {
            DialogInput.show(fm = childFragmentManager, message = "Name")
        }

        presenter.onLoad()
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