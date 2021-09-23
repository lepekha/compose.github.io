package ua.com.compose.view.main.history

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.fragment_image_history.*
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named
import ua.com.compose.R
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.nonNull
import ua.com.compose.extension.observe
import ua.com.compose.extension.setVibrate
import ua.com.compose.image_compress.main.ImageCompressFragment
import ua.com.compose.mvp.BaseMvvmFragment



class ImageHistoryFragment : BaseMvvmFragment() {

    companion object {

        private const val BUNDLE_KEY_IMAGE_URI = "BUNDLE_KEY_IMAGE_URI"

        fun newInstance(uri: Uri?): ImageHistoryFragment {
            return ImageHistoryFragment().apply {
                arguments = bundleOf(
                    BUNDLE_KEY_IMAGE_URI to uri
                )
            }
        }
    }

    private val viewModule: ImageHistoryViewModule by lazy {
        val scope = getKoin().getOrCreateScope(
            "app", named("app")
        )
        scope.get()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_image_history, container, false)
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
            viewModule.addImageToHistory(bundle.getParcelable<Uri>(ImageCompressFragment.BUNDLE_KEY_IMAGE_URI))
        }

        btnSave.setVibrate(EVibrate.BUTTON)
        btnSave.setOnClickListener {
            viewModule.pressSave()
        }

        btnRemove.setVibrate(EVibrate.BUTTON)
        btnRemove.setOnClickListener {
            viewModule.pressRemove()
        }

        btnShare.setVibrate(EVibrate.BUTTON)
        btnShare.setOnClickListener {
        }

        val c1 = ConstraintSet().apply {
            this.clone(container)
        }

        val c2 = ConstraintSet().apply {
            this.clone(requireContext(), R.layout.fragment_image_history_full)
        }

        imgPreview.setOnClickListener {
            TransitionManager.beginDelayedTransition(container)
            val c = if(isOpen) c2 else c1
            c.applyTo(container)
            isOpen = !isOpen
        }

        viewModule.onCreate(uri = inputUri)
    }

    private var isOpen = false

    fun addImageToHistory(uri: Uri?){
        viewModule.addImageToHistory(uri)
    }
}