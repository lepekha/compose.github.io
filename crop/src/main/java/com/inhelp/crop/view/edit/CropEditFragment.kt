package com.inhelp.crop.view.edit

import android.content.Intent.getIntent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.inhelp.base.mvp.BaseMvpFragment
import com.inhelp.color.ColorFragment
import com.inhelp.color.util.Util
import com.inhelp.crop.R
import com.inhelp.crop.data.ECrop
import com.inhelp.crop.view.share.CropShareFragment
import com.inhelp.crop.view.share.CropSharePresenter
import com.inhelp.gallery.main.FragmentGallery
import kotlinx.android.synthetic.main.fragment_crop.*
import kotlinx.android.synthetic.main.fragment_crop_edit.*
import kotlinx.android.synthetic.main.fragment_crop_edit.btnBefore
import kotlinx.android.synthetic.main.fragment_crop_edit.imgView
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named
import replace
import java.io.ByteArrayOutputStream


class CropEditFragment : BaseMvpFragment<CropEditView, CropEditPresenter>(), CropEditView, ColorFragment.ColorListener {

    companion object {

        fun newInstance() = CropEditFragment()
    }

    override val presenter: CropEditPresenter by lazy {
        val scope = getKoin().getScope("crop")
        scope.get()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_crop_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getCurrentContext().getString(R.string.fragment_title_crop_edit))

        presenter.onLoadImage()

        btnBefore.setOnClickListener {
            getCurrentActivity().onBackPressed()
        }

        btnDone.setOnClickListener {
            presenter.pressDone()
        }

        btnColor.setOnClickListener {
            ColorFragment.newInstance(color = Color.WHITE, targetFragment = this).show(requireFragmentManager(), "ColorFragment")
        }

        btnBlur.setOnClickListener {
            presenter.pressBlur()
        }
    }

    override fun setImageBitmap(bitmap: Bitmap) {
        Glide
                .with(imgView.context)
                .load(bitmap)
                .thumbnail(0.3f)
                .into(imgView)
    }

    override fun setImageBackground(bitmap: Bitmap) {
        imgView.background = bitmap.toDrawable(getCurrentContext().resources)
    }

    override fun navigateToShare() {
        getCurrentActivity().supportFragmentManager.replace(fragment = CropShareFragment.newInstance(), addToBackStack = true)
    }

    override fun setVisibleEditButton(isVisible: Boolean) {
        btnBlur.isVisible = isVisible
        btnColor.isVisible = isVisible
    }

    override fun onColorPick(color: Int) {
        presenter.onColorPick(color = color)
    }
}