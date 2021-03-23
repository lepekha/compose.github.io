package com.dali.rotate.view.main

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.dali.rotate.R
import com.dali.rotate.di.Scope
import com.inhelp.base.mvp.BaseMvpFragment
import com.inhelp.crop_image.main.data.AnimatableRectF
import com.inhelp.extension.EVibrate
import com.inhelp.extension.createImageIntent
import com.inhelp.extension.setVibrate
import com.inhelp.gallery.main.FragmentGallery
import data.BottomMenu
import data.Menu
import jp.co.cyberagent.android.gpuimage.GPUImage
import kotlinx.android.synthetic.main.fragment_rotate_main.*
import replace


class ImageRotateFragment : BaseMvpFragment<ImageRotateView, ImageRotatePresenter>(), ImageRotateView {

    companion object {
        fun newInstance(): ImageRotateFragment {
            return ImageRotateFragment()
        }
    }

    override val presenter: ImageRotatePresenter by lazy { Scope.ROTATE .get() }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_rotate_main, container, false)
    }

    private val btnShare by lazy {
        BottomMenu(iconResId = com.inhelp.theme.R.drawable.ic_share) {
            presenter.pressShare()
        }
    }

    private val btnDownload by lazy {
        BottomMenu(iconResId = com.inhelp.theme.R.drawable.ic_download) {
            presenter.pressSave()
        }
    }

    private val btnGallery by lazy {
        BottomMenu(iconResId = com.inhelp.theme.R.drawable.ic_gallery) {
            getCurrentActivity().supportFragmentManager.replace(fragment = FragmentGallery.newInstance(targetFragment = this), addToBackStack = true)
        }
    }

    override fun createBottomMenu(): MutableList<Menu> {
        return mutableListOf<Menu>().apply {
            this.add(btnGallery)
            this.add(btnShare)
            this.add(btnDownload)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getCurrentContext().getString(R.string.fragment_image_rotate_title))

        if (arguments == null) {
            getCurrentActivity().supportFragmentManager.replace(fragment = FragmentGallery.newInstance(targetFragment = this), addToBackStack = true)
        } else {
            presenter.onLoad(uriString = arguments?.getString(FragmentGallery.ARGUMENT_ONE_URI))
        }

        btnFlip.setVibrate(EVibrate.BUTTON)
        btnFlip.setOnClickListener {
            presenter.pressFlip()
        }

        btnRotateLeft.setVibrate(EVibrate.BUTTON)
        btnRotateLeft.setOnClickListener {
            presenter.pressRotateLeft()
        }

        btnRotateRight.setVibrate(EVibrate.BUTTON)
        btnRotateRight.setOnClickListener {
            presenter.pressRotateRight()
        }
    }

    override fun setFlipXToImage(scale: Float) {
        val animateRotation: ObjectAnimator = ObjectAnimator.ofFloat(imgView, "scaleX", imgView.scaleX, scale).setDuration(200)
        animateRotation.start()
    }

    override fun setFlipYToImage(scale: Float) {
        val animateRotation: ObjectAnimator = ObjectAnimator.ofFloat(imgView, "scaleY", imgView.scaleY, scale).setDuration(200)
        animateRotation.start()
    }

    override fun setRotateToImage(angel: Float) {
        val animateRotation: ObjectAnimator = ObjectAnimator.ofFloat(imgView, "rotation", imgView.rotation, angel).setDuration(200)
        animateRotation.start()
    }

    override fun createShareIntent(uri: Uri) {
        getCurrentActivity().createImageIntent(uri)
    }

    override fun setImage(uri: Uri){
        Glide
                .with(imgView.context)
                .asBitmap()
                .format(DecodeFormat.PREFER_RGB_565)
                .load(uri)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        imgView.setImageBitmap(resource)
                        presenter.onResourceLoad(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
    }

    override fun onDestroy() {
        super.onDestroy()
        Scope.ROTATE.close()
    }
}