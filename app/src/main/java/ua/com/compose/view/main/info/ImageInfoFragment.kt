package ua.com.compose.view.main.info

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.fragment_image_info.*
import org.koin.android.ext.android.getKoin
import org.koin.core.qualifier.named
import ua.com.compose.R
import ua.com.compose.extension.*
import ua.com.compose.image_compress.main.ImageCompressFragment
import ua.com.compose.mvp.BaseMvvmFragment
import ua.com.compose.navigator.replace


class ImageInfoFragment : BaseMvvmFragment() {

    companion object {

        private const val BUNDLE_KEY_IMAGE_URI = "BUNDLE_KEY_IMAGE_URI"

        fun newInstance(uri: Uri?): ImageInfoFragment {
            return ImageInfoFragment().apply {
                arguments = bundleOf(
                    BUNDLE_KEY_IMAGE_URI to uri
                )
            }
        }
    }

    private val viewModule: ImageInfoViewModule by lazy {
        val scope = getKoin().getOrCreateScope(
            "app", named("app")
        )
        scope.get()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_image_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setVisibleBack(false)
        val inputUri = arguments?.getParcelable(BUNDLE_KEY_IMAGE_URI) as? Uri
        container.isVisible = false

        viewModule.mainImage.nonNull().observe(this) { uri ->
            container.isVisible = true
            Glide.with(requireContext()).load(uri).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).centerInside().thumbnail(0.1f).into(imgPreview)
        }

        viewModule.alert.observe(this) {
            it?.let {
                showAlert(it)
            }
        }

        viewModule.visible.observe(this){
            container.isVisible = it
        }

        setFragmentResultListener(ImageCompressFragment.REQUEST_KEY) { _, bundle ->
            viewModule.addImage(bundle.getParcelable<Uri>(ImageCompressFragment.BUNDLE_KEY_IMAGE_URI))
        }

        btnSave.setVibrate(EVibrate.BUTTON)
        btnSave.setOnClickListener {
            viewModule.pressSave()
        }

        btnRemove.setVibrate(EVibrate.BUTTON)
        btnRemove.setOnClickListener {
            viewModule.pressRemove()
        }

        btnShareImage.setVibrate(EVibrate.BUTTON)
        btnShareImage.setOnClickListener {
            viewModule.imageHolder.image?.let {
                requireActivity().createImageIntent(it)
            }
        }

        imgPreview.setVibrate(EVibrate.BUTTON)
        imgPreview.setOnClickListener {
            requireActivity().supportFragmentManager.replace(
                fragment = ua.com.compose.other_image_info.main.ImageInfoFragment.newInstance(uri = viewModule.imageHolder.image),
                addToBackStack = true
            )
        }

        viewModule.onCreate(uri = inputUri)
    }

}