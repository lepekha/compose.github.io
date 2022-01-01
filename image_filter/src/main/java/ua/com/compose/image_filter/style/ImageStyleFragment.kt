package ua.com.compose.image_filter.style

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.drawToBitmap
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.module_image_filter_fragment_style.*
import ua.com.compose.image_filter.di.Scope
import ua.com.compose.mvp.BaseMvpFragment
import ua.com.compose.gallery.main.FragmentGallery
import ua.com.compose.image_filter.R
import ua.com.compose.image_maker.FrameImageView
import ua.com.compose.mvp.data.BottomMenu
import ua.com.compose.mvp.data.Menu


class ImageStyleFragment : BaseMvpFragment<ImageStyleView, ImageStylePresenter>(), ImageStyleView {

    companion object {

        const val REQUEST_KEY = "REQUEST_KEY_IMAGE"

        const val BUNDLE_KEY_IMAGE_URI = "BUNDLE_KEY_IMAGE_URI"

        fun newInstance(uri: Uri?): ImageStyleFragment {
            return ImageStyleFragment().apply {
                arguments = bundleOf(
                    BUNDLE_KEY_IMAGE_URI to uri
                )
            }
        }
    }


    override val presenter: ImageStylePresenter by lazy { Scope.IMAGE_STYLE.get() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.module_image_filter_fragment_style, container, false)
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

    private fun initStyleList() {
        list.layoutManager = LinearLayoutManager(getCurrentContext(), RecyclerView.HORIZONTAL, false)
        list.adapter = ImageFilterHistoryRvAdapter(presenter.images.toMutableList()){
            presenter.pressImageHistory(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getCurrentContext().getString(R.string.module_image_filter_title))
        initStyleList()

        setFragmentResultListener(FragmentGallery.REQUEST_KEY) { _, bundle ->
            val uris = (bundle.getSerializable(FragmentGallery.BUNDLE_KEY_IMAGES) as List<*>).filterIsInstance<Uri>()
            presenter.onAddImage(uris)
        }

        imgView.setOnTouchListener { _, event ->
            when(event.action){
                MotionEvent.ACTION_DOWN -> presenter.pressImageDown()
                MotionEvent.ACTION_UP -> presenter.pressImageUp()
            }
            true
        }

        imgView.setImageChangeListener(object : FrameImageView.OnImageChangeListener {
            override fun imageSampleChange(bitmap: Bitmap?) {
                if(imgView.drawable == null) return
                presenter.onSampleLoad(imgView.drawToBitmap())
            }
        })

        val inputUri = (arguments?.getParcelable(BUNDLE_KEY_IMAGE_URI) as? Uri)

        presenter.onCreate(uri = inputUri)
    }

    override fun updateList() {
        initStyleList()
    }

    override fun backPress(byBack: Boolean): Boolean {
        presenter.pressBack()
        return true
    }

    override fun openGallery() {
        FragmentGallery.show(fm = getCurrentActivity().supportFragmentManager, isMultiSelect = false)
    }

    override fun saveToResult(uri: Uri) {
        setFragmentResult(REQUEST_KEY, bundleOf(BUNDLE_KEY_IMAGE_URI to uri))
    }

    override fun setImage(uri: Uri) {
        Glide.with(requireContext().applicationContext).load(uri).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(imgView)

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

    override fun setImage(bmp: Bitmap) {
        imgView.setImageBitmap(bmp)
    }

    override fun onDestroy() {
        super.onDestroy()
        Scope.IMAGE_STYLE.close()
    }
}