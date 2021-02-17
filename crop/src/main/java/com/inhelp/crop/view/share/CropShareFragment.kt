package com.inhelp.crop.view.share

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import clearAllFragments
import com.bumptech.glide.Glide
import com.inhelp.base.mvp.BaseMvpFragment
import com.inhelp.color.ColorFragment
import com.inhelp.crop.R
import com.inhelp.extension.createImageIntent
import com.inhelp.extension.createInstagramIntent
import com.inhelp.extension.toast
import kotlinx.android.synthetic.main.fragment_crop_share.*
import kotlinx.coroutines.*
import org.koin.android.ext.android.getKoin


class CropShareFragment : BaseMvpFragment<CropShareView, CropSharePresenter>(), CropShareView, ColorFragment.ColorListener {

    companion object {
        fun newInstance() = CropShareFragment()
    }

    override val presenter: CropSharePresenter by lazy {
        val scope = getKoin().getScope("crop")
        scope.get()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_crop_share, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getCurrentContext().getString(R.string.fragment_title_crop_share))
        presenter.onLoadImage()

        btnSave.setOnClickListener {
            presenter.pressSave()
        }

        btnShare.setOnClickListener {
            presenter.pressShare()
        }

        btnInstagram.setOnClickListener {
            presenter.pressInstagram()
        }
    }

    override fun setDownloadProgressVisible(isVisible: Boolean) = GlobalScope.launch(Dispatchers.Main) {
        btnSave.isClickable = false
        if(isVisible){
            btnSave.setImageResource(R.drawable.animate_progress_bar)
        }else{
            btnSave.setImageResource(R.drawable.ic_download_done)
        }
    }

    override fun setImageBitmap(bitmap: Bitmap) {
        Glide
                .with(imgView.context)
                .load(bitmap)
                .thumbnail(0.3f)
                .into(imgView)
    }

    override fun createShareIntent(uri: Uri) {
        getCurrentActivity().createImageIntent(uri)
    }

    override fun createInstagramIntent(uri: Uri) {
        getCurrentActivity().createInstagramIntent(uri)
    }

    override fun onDestroy() {
        super.onDestroy()
        getKoin().getScopeOrNull("crop")?.close()
    }

    override fun backPress(): Boolean {
        backToMain()
        return true
    }
}