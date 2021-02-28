package com.inhelp.instagram.view.edit

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.inhelp.base.mvp.BaseMvpFragment
import com.inhelp.color.ColorFragment
import com.inhelp.instagram.R
import com.inhelp.instagram.view.main.CropFragment
import com.inhelp.instagram.view.share.CropShareFragment
import kotlinx.android.synthetic.main.fragment_crop_edit.*
import kotlinx.android.synthetic.main.fragment_crop_edit.btnBefore
import kotlinx.android.synthetic.main.fragment_crop_edit.imgView
import org.koin.android.ext.android.getKoin
import replace


class CropEditFragment : BaseMvpFragment<CropEditView, CropEditPresenter>(), CropEditView, ColorFragment.ColorListener {

    companion object {

        fun newInstance() = CropEditFragment()
    }

    override val presenter: CropEditPresenter by lazy {
        val scope = getKoin().getScope("crop")
        scope.get()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(getCurrentContext()).inflateTransition(R.transition.shared_image)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_crop_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getCurrentContext().getString(R.string.fragment_title_crop_edit))
        ViewCompat.setTransitionName(imgView, CropFragment.TRANSITION_IMAGE_NAME)

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
                .listener(object : RequestListener<Drawable>{
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        startPostponedEnterTransition()
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        startPostponedEnterTransition()
                        return false
                    }
                })
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