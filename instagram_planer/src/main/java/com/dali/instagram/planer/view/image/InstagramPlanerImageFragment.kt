package com.dali.instagram.planer.view.image

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResultListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.dali.instagram.planer.R
import com.dali.instagram.planer.di.Scope
import com.inhelp.base.mvp.BaseMvpFragment
import com.inhelp.dialogs.main.dialogs.DialogConfirmation
import com.inhelp.gallery.main.FragmentGallery
import data.BottomMenu
import data.Menu
import kotlinx.android.synthetic.main.fragment_instagram_planer_image.*


class InstagramPlanerImageFragment : BaseMvpFragment<InstagramPlanerImageView, InstagramPlanerImagePresenter>(), InstagramPlanerImageView {

    companion object {

        private const val BUNDLE_IMAGE_URI = "BUNDLE_IMAGE_URI"

        fun newInstance(uri: Uri): InstagramPlanerImageFragment {
            return InstagramPlanerImageFragment().apply {
                this.arguments = bundleOf(BUNDLE_IMAGE_URI to uri)
            }
        }
    }

    override val presenter: InstagramPlanerImagePresenter by lazy {
        Scope.INSTAGRAM.get()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_instagram_planer_image, container, false)
    }

    private val btnGallery = BottomMenu(iconResId = com.inhelp.theme.R.drawable.ic_gallery) {
        FragmentGallery.show(fm = getCurrentActivity().supportFragmentManager, isMultiSelect = false)
    }

    private val btnDelete = BottomMenu(iconResId = com.inhelp.theme.R.drawable.ic_delete) {
        val request = DialogConfirmation.show(fm = requireActivity().supportFragmentManager, message = "Remove this image?")
        setFragmentResultListener(request) { _, bundle ->
            if(bundle.getBoolean(DialogConfirmation.BUNDLE_KEY_ANSWER)){
                presenter.pressDelete()
            }
        }
    }

//    private val btnDownload by lazy {
//        BottomMenu(iconResId = com.inhelp.theme.R.drawable.ic_download) {
//        }
//    }
//
//    private val btnShare by lazy {
//        BottomMenu(iconResId = com.inhelp.theme.R.drawable.ic_share) {
//        }
//    }
//
//    private val btnInstagram by lazy {
//        BottomMenu(iconResId = com.inhelp.theme.R.drawable.ic_instagram) {
//        }
//    }

    override fun createBottomMenu(): MutableList<Menu> {
        return mutableListOf(btnGallery, btnDelete)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(requireContext().getString(R.string.module_instagram_palaner_image_title))

        setFragmentResultListener(FragmentGallery.REQUEST_KEY) { _, bundle ->
            presenter.onAddImages((bundle.getSerializable(FragmentGallery.BUNDLE_KEY_IMAGES) as List<*>).filterIsInstance<Uri>())
        }

        loadImage()
    }

    override fun loadImage(){
        Glide
                .with(image.context)
                .load(this.arguments?.getParcelable<Uri>(BUNDLE_IMAGE_URI))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .thumbnail(0.1f)
                .into(image)
    }

}