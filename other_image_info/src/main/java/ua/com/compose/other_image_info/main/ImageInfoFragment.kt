package ua.com.compose.other_image_info.main

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.nonNull
import ua.com.compose.extension.observe
import ua.com.compose.extension.setVibrate
import ua.com.compose.mvp.BaseMvvmFragment
import ua.com.compose.other_image_info.R
import ua.com.compose.other_image_info.databinding.ModuleOtherImageInfoFragmentMainBinding


class ImageInfoFragment : BaseMvvmFragment() {

    companion object {

        private const val BUNDLE_KEY_IMAGE_URI = "BUNDLE_KEY_IMAGE_URI"
        private const val REQUEST_KEY = "REQUEST_KEY"

        fun newInstance(uri: Uri?): ImageInfoFragment {
            return ImageInfoFragment().apply {
                arguments = bundleOf(
                    BUNDLE_KEY_IMAGE_URI to uri
                )
            }
        }
    }

    private var binding: ModuleOtherImageInfoFragmentMainBinding? = null

    private val viewModule: ImageInfoViewModule by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ModuleOtherImageInfoFragmentMainBinding.inflate(inflater)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(requireContext().getString(R.string.module_other_image_info_fragment_title))
        setVisibleBack(true)

        viewModule.mainImage.nonNull().observe(this) { uri ->
            binding?.container?.isVisible = true
            binding?.imgPreview?.let {
                Glide.with(requireContext()).load(uri).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).centerInside().thumbnail(0.1f).into(it)
            }
        }

        viewModule.alert.observe(this) {
            it?.let {
                showAlert(it)
            }
        }

        viewModule.visible.observe(this){
            binding?.container?.isVisible = it
        }

        val inputUri = arguments?.getParcelable(BUNDLE_KEY_IMAGE_URI) as? Uri
        viewModule.onCreate(uri = inputUri)
    }

    override fun backPress(byBack: Boolean): Boolean {
        return false
    }
}