package ua.com.compose.instagram_planer.view.image

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.module_instagram_planer_fragment_instagram_planer_image.*
import ua.com.compose.extension.*
import ua.com.compose.instagram_planer.di.Scope
import ua.com.compose.mvp.data.BottomMenu
import ua.com.compose.mvp.data.Menu
import ua.com.compose.instagram_planer.R
import ua.com.compose.mvp.BaseMvvmFragment


class InstagramPlanerImageFragment : BaseMvvmFragment() {

    companion object {

        private const val BUNDLE_KEY_IMAGE_ID = "BUNDLE_KEY_IMAGE_ID"

        fun newInstance(imageId: Long): InstagramPlanerImageFragment {
            return InstagramPlanerImageFragment().apply {
                this.arguments = bundleOf(BUNDLE_KEY_IMAGE_ID to imageId)
            }
        }
    }

    private val viewModel: InstagramPlanerImageViewModel by lazy {
        Scope.INSTAGRAM.get()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.module_instagram_planer_fragment_instagram_planer_image, container, false)
    }

    private val btnDownload by lazy {
        BottomMenu(iconResId = ua.com.compose.R.drawable.ic_download) {
            viewModel.pressSave()
        }
    }

    private val btnShare by lazy {
        BottomMenu(iconResId = ua.com.compose.R.drawable.ic_share) {
            viewModel.currentImage?.uri?.toUri()?.toFile()?.let {
                val uri = FileProvider.getUriForFile(requireContext(), "ua.com.compose.fileprovider", it)
                requireActivity().createImageIntent(uri)
            }
        }
    }

    private val btnInstagram by lazy {
        BottomMenu(iconResId = ua.com.compose.R.drawable.ic_instagram) {
            viewModel.currentImage?.uri?.toUri()?.toFile()?.let {
                val uri = FileProvider.getUriForFile(requireContext(), "ua.com.compose.fileprovider", it)
                requireActivity().createInstagramIntent(uri)
            }
        }
    }

    override fun createBottomMenu(): MutableList<Menu> {
        return mutableListOf(btnDownload, btnShare, btnInstagram)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(requireContext().getString(R.string.module_instagram_palaner_image_title))

        editText.onTextChangedListener {
            viewModel.onTextChange(text = it)
        }

        btnCopy.setOnClickListener {
            requireContext().clipboardCopy(text = editText.text.toString())
            showAlert(R.string.module_instagram_palaner_text_copied)
        }

        viewModel.onCreate(id = arguments?.getLong(BUNDLE_KEY_IMAGE_ID, -1) ?: -1)

        viewModel.image.observe(viewLifecycleOwner){
            it?.let { uri ->
                Glide
                    .with(image.context)
                    .load(uri)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .thumbnail(0.1f)
                    .into(image)
            }
        }

        viewModel.createAlert.observe(viewLifecycleOwner){
            it?.let { showAlert(it) }
        }

        viewModel.text.observe(viewLifecycleOwner){
            editText.setText(it)
        }

        viewModel.textCountSymbol.observe(viewLifecycleOwner){
            txtSymbolCount.text = "$it/2200"
            btnCopy.isVisible = it > 0
            if(it>2200){
                txtSymbolCount.setTextColor(requireContext().getColorFromAttr(R.attr.color_7))
            }else{
                txtSymbolCount.setTextColor(requireContext().getColorFromAttr(R.attr.color_12))
            }
        }

        viewModel.textCountHashtag.observe(viewLifecycleOwner){
            txtHashtagCount.text = "# $it/30"
            if(it>30){
                txtHashtagCount.setTextColor(requireContext().getColorFromAttr(R.attr.color_7))
            }else{
                txtHashtagCount.setTextColor(requireContext().getColorFromAttr(R.attr.color_12))
            }
        }

        viewModel.textCountMail.observe(viewLifecycleOwner){
            txtMailCount.text = "@ $it/30"
            if(it>30){
                txtMailCount.setTextColor(requireContext().getColorFromAttr(R.attr.color_7))
            }else{
                txtMailCount.setTextColor(requireContext().getColorFromAttr(R.attr.color_12))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideKeyboard()
    }
}