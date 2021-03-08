package com.inhelp.instagram.view.edit

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.inhelp.base.mvp.BaseMvpFragment
import com.inhelp.color.ColorFragment
import com.inhelp.instagram.R
import com.inhelp.instagram.di.Scope
import com.inhelp.instagram.view.save.NoCropSaveFragment
import data.BottomMenu
import data.Menu
import kotlinx.android.synthetic.main.fragment_instagram_no_crop_edit.*
import replace


class NoCropEditFragment : BaseMvpFragment<NoCropEditView, NoCropEditPresenter>(), NoCropEditView, ColorFragment.ColorListener {

    companion object {

        fun newInstance() = NoCropEditFragment()
    }

    override val presenter: NoCropEditPresenter by lazy {
        Scope.INSTAGRAM.get()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_instagram_no_crop_edit, container, false)
    }

    private val btnDone by lazy {
        BottomMenu(iconResId = com.inhelp.theme.R.drawable.ic_next) {
            presenter.pressDone()
        }
    }

    private val btnColor by lazy {
        BottomMenu(iconResId = com.inhelp.theme.R.drawable.ic_color_fill) {
            ColorFragment.newInstance(color = Color.WHITE, targetFragment = this).show(requireFragmentManager(), "ColorFragment")
        }
    }

    private val btnBlur by lazy {
        BottomMenu(iconResId = com.inhelp.theme.R.drawable.ic_blur) {
            presenter.pressBlur()
        }
    }

    override fun createBottomMenu(): MutableList<Menu> {
        return mutableListOf<Menu>().apply {
            this.add(btnColor)
            this.add(btnBlur)
            this.add(btnDone)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getCurrentContext().getString(R.string.fragment_title_crop_edit))

        presenter.onLoadImage()
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
        getCurrentActivity().supportFragmentManager.replace(fragment = NoCropSaveFragment.newInstance(), addToBackStack = true)
    }

    override fun onColorPick(color: Int) {
        presenter.onColorPick(color = color)
    }
}