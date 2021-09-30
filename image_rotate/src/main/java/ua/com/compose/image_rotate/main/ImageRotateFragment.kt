package ua.com.compose.image_rotate.main

import android.animation.ObjectAnimator
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import ua.com.compose.image_rotate.di.Scope
import ua.com.compose.mvp.BaseMvpFragment
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.createImageIntent
import ua.com.compose.gallery.main.FragmentGallery
import ua.com.compose.mvp.data.BottomMenu
import ua.com.compose.mvp.data.Menu
import kotlinx.android.synthetic.main.module_image_rotate_fragment_rotate_main.*
import ua.com.compose.extension.setVibrate
import ua.com.compose.image_rotate.R
import ua.com.compose.mvp.BaseMvpView


class ImageRotateFragment : BaseMvpFragment<ImageRotateView, ImageRotatePresenter>(), ImageRotateView {

    companion object {
        const val REQUEST_KEY = "REQUEST_KEY_IMAGE"
        private const val BUNDLE_KEY_IMAGE_URI = "BUNDLE_KEY_IMAGE_URI"

        fun newInstance(uri: Uri?): ImageRotateFragment {
            return ImageRotateFragment().apply {
                arguments = bundleOf(
                    BUNDLE_KEY_IMAGE_URI to uri
                )
            }
        }
    }

    override val presenter: ImageRotatePresenter by lazy { Scope.ROTATE .get() }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.module_image_rotate_fragment_rotate_main, container, false)
    }

    private val btnDone by lazy {
        BottomMenu(iconResId = ua.com.compose.R.drawable.ic_done) {
            presenter.pressDone()
        }
    }

    private val btnGallery by lazy {
        BottomMenu(iconResId = ua.com.compose.R.drawable.ic_gallery) {
            openGallery()
        }
    }

    override fun createBottomMenu(): MutableList<Menu> {
        return mutableListOf<Menu>().apply {
            this.add(btnGallery)
            this.add(btnDone)
        }
    }

    override fun saveToResult(uri: Uri) {
        setFragmentResult(REQUEST_KEY, bundleOf(BUNDLE_KEY_IMAGE_URI to uri))
        (requireActivity() as BaseMvpView).backPress()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getCurrentContext().getString(R.string.module_image_rotate_fragment_image_rotate_title))

        setFragmentResultListener(FragmentGallery.REQUEST_KEY) { _, bundle ->
            presenter.onAddImage((bundle.getSerializable(FragmentGallery.BUNDLE_KEY_IMAGES) as List<*>).filterIsInstance<Uri>())
        }

        imgView.isFrameEnable = true

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

        val inputUri = arguments?.getParcelable(BUNDLE_KEY_IMAGE_URI) as? Uri

        presenter.onCreate(uri = inputUri)
    }

    override fun openGallery() {
        FragmentGallery.show(fm = getCurrentActivity().supportFragmentManager, isMultiSelect = false)
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

    override fun setImage(uri: Uri) {
        Glide.with(requireContext().applicationContext).load(uri).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).thumbnail(0.1f).into(imgView)

        Glide.with(requireContext().applicationContext)
                .asBitmap()
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(object: CustomTarget<Bitmap>() {
                    override fun onLoadCleared(placeholder: Drawable?) {}

                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        presenter.onResourceLoad(resource)
                    }
                })

    }

    override fun onDestroy() {
        super.onDestroy()
        Scope.ROTATE.close()
    }
}