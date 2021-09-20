package ua.com.compose.instagram_planer.view.image

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResultListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.module_instagram_planer_fragment_instagram_planer_image.*
import ua.com.compose.instagram_planer.di.Scope
import ua.com.compose.mvp.BaseMvpFragment
import ua.com.compose.dialog.dialogs.DialogConfirmation
import ua.com.compose.gallery.main.FragmentGallery
import ua.com.compose.mvp.data.BottomMenu
import ua.com.compose.mvp.data.Menu
import ua.com.compose.instagram_planer.R


class InstagramPlanerImageFragment : BaseMvpFragment<InstagramPlanerImageView, InstagramPlanerImagePresenter>(), InstagramPlanerImageView {

    companion object {

        private const val BUNDLE_IMAGE_URI = "BUNDLE_IMAGE_URI"

        fun newInstance(): InstagramPlanerImageFragment {
            return InstagramPlanerImageFragment()
        }
    }

    override val presenter: InstagramPlanerImagePresenter by lazy {
        Scope.INSTAGRAM.get()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.module_instagram_planer_fragment_instagram_planer_image, container, false)
    }

    private val btnGallery = BottomMenu(iconResId = ua.com.compose.R.drawable.ic_gallery) {
        FragmentGallery.show(fm = getCurrentActivity().supportFragmentManager, isMultiSelect = false)
    }

    private val btnDelete = BottomMenu(iconResId = ua.com.compose.R.drawable.ic_delete) {
        val request = DialogConfirmation.show(fm = requireActivity().supportFragmentManager, message = "Remove this image?")
        setFragmentResultListener(request) { _, bundle ->
            if(bundle.getBoolean(DialogConfirmation.BUNDLE_KEY_ANSWER)){
                presenter.pressDelete()
            }
        }
    }

//    private val btnDownload by lazy {
//        BottomMenu(iconResId = ua.com.compose.R.drawable.ic_download) {
//        }
//    }
//
//    private val btnShare by lazy {
//        BottomMenu(iconResId = ua.com.compose.R.drawable.ic_share) {
//        }
//    }
//
//    private val btnInstagram by lazy {
//        BottomMenu(iconResId = ua.com.compose.R.drawable.ic_instagram) {
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

        presenter.onCreate()
    }

    override fun setImage(uri: Uri){
        Glide
                .with(image.context)
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .thumbnail(0.1f)
                .into(image)
    }

}