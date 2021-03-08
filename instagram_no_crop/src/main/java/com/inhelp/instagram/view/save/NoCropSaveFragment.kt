package com.inhelp.instagram.view.save

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.inhelp.base.mvp.BaseMvpFragment
import com.inhelp.color.ColorFragment
import com.inhelp.instagram.R
import com.inhelp.extension.createImageIntent
import com.inhelp.extension.createInstagramIntent
import com.inhelp.instagram.di.Scope
import data.BottomMenu
import data.Menu
import kotlinx.android.synthetic.main.fragment_instagram_no_crop_share.*
import kotlinx.coroutines.*


class NoCropSaveFragment : BaseMvpFragment<NoCropSaveView, NoCropSavePresenter>(), NoCropSaveView, ColorFragment.ColorListener {

    companion object {
        fun newInstance() = NoCropSaveFragment()
    }

    override val presenter: NoCropSavePresenter by lazy {
        Scope.INSTAGRAM.get()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_instagram_no_crop_share, container, false)
    }

    private val btnDownload by lazy {
        BottomMenu(iconResId = com.inhelp.theme.R.drawable.ic_download) {
            presenter.pressSave()
        }
    }

    private val btnShare by lazy {
        BottomMenu(iconResId = com.inhelp.theme.R.drawable.ic_share) {
            presenter.pressShare()
        }
    }

    private val btnInstagram by lazy {
        BottomMenu(iconResId = com.inhelp.theme.R.drawable.ic_instagram) {
            presenter.pressInstagram()
        }
    }

    override fun createBottomMenu(): MutableList<Menu> {
        return mutableListOf<Menu>().apply {
            this.add(btnShare)
            this.add(btnInstagram)
            this.add(btnDownload)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getCurrentContext().getString(R.string.fragment_title_crop_share))

        presenter.onLoadImage()
    }

    override fun setImageBitmap(bitmap: Bitmap) {
        Glide
                .with(imgView.context)
                .load(bitmap)
                .thumbnail(0.3f)
                .into(imgView)
    }

    override fun setDownloadDone() {
        btnDownload.iconResId = R.drawable.ic_download_done
        btnDownload.isEnabled = false
        updateBottomMenu()
    }

    override fun createShareIntent(uri: Uri) {
        getCurrentActivity().createImageIntent(uri)
    }

    override fun createInstagramIntent(uri: Uri) {
        getCurrentActivity().createInstagramIntent(uri)
    }

    override fun onDestroy() {
        super.onDestroy()
        Scope.INSTAGRAM.close()
    }

    override fun backPress(): Boolean {
        backToMain()
        return true
    }
}