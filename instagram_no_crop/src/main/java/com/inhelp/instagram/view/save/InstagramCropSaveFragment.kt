package com.inhelp.instagram.view.save

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.inhelp.base.mvp.BaseMvpFragment
import com.inhelp.color.ColorFragment
import com.inhelp.dialogs.main.dialogs.DialogColor
import com.inhelp.dialogs.main.dialogs.DialogConfirmation
import com.inhelp.extension.*
import com.inhelp.instagram.R
import com.inhelp.instagram.di.Scope
import data.BottomMenu
import data.Menu
import kotlinx.android.synthetic.main.fragment_instagram_no_crop_share.*
import kotlinx.android.synthetic.main.fragment_instagram_no_crop_share.imgView


class InstagramCropSaveFragment : BaseMvpFragment<InstagramCropSaveView, InstagramCropSavePresenter>(), InstagramCropSaveView {

    companion object {
        fun newInstance() = InstagramCropSaveFragment()
    }

    override val presenter: InstagramCropSavePresenter by lazy {
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
        setTitle(getCurrentContext().getString(R.string.fragment_title_no_crop_save_images))

        btnBlur.setVibrate(EVibrate.BUTTON)
        btnBlur.setOnClickListener {
            btnColorFill.setColorFilter(requireContext().getColorFromAttr(R.attr.color_3))
            btnBlur.setColorFilter(requireContext().getColorFromAttr(R.attr.color_5))
            presenter.pressBlur()
        }

        btnColorFill.setVibrate(EVibrate.BUTTON)
        btnColorFill.setOnClickListener {
            val request = DialogColor.show(fm = getCurrentActivity().supportFragmentManager, color = Color.RED)
            setFragmentResultListener(request) { _, bundle ->
                presenter.onColorPick(color = bundle.getInt(DialogColor.BUNDLE_KEY_ANSWER_COLOR))
                btnColorFill.setColorFilter(requireContext().getColorFromAttr(R.attr.color_5))
                btnBlur.setColorFilter(requireContext().getColorFromAttr(R.attr.color_3))
            }
        }

        presenter.onLoadImage()
    }

    override fun setImageBitmap(bitmap: Bitmap) {
        Glide
                .with(imgView.context)
                .load(bitmap)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .thumbnail(0.1f)
                .into(imgView)
    }

    override fun createShareIntent(uri: Uri) {
        getCurrentActivity().createImageIntent(uri)
    }

    override fun createInstagramIntent(uri: Uri) {
        getCurrentActivity().createInstagramIntent(uri)
    }

    override fun setImageBackground(bitmap: Bitmap) {
        imgView.background = bitmap.toDrawable(getCurrentContext().resources)
    }

    override fun setVisibleEdit(isVisible: Boolean) {
        btnBlur.isVisible = isVisible
        btnColorFill.isVisible = isVisible
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