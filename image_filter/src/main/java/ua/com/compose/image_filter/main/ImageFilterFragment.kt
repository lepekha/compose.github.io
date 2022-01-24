package ua.com.compose.image_filter.main

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.net.Uri
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import jp.co.cyberagent.android.gpuimage.GLTextureView
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.GPUImageView
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBrightnessFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilterGroup
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGrayscaleFilter
import kotlinx.android.synthetic.main.module_image_filter_fragment_filter.*
import ua.com.compose.dialog.dialogs.DialogConfirmation
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.vibrate
import ua.com.compose.image_filter.di.Scope
import ua.com.compose.mvp.BaseMvpFragment
import ua.com.compose.gallery.main.FragmentGallery
import ua.com.compose.image_filter.R
import ua.com.compose.image_filter.data.ImageFilter
import ua.com.compose.image_maker.FrameImageView
import ua.com.compose.mvp.BaseMvpActivity
import ua.com.compose.mvp.data.BottomMenu
import ua.com.compose.mvp.data.Menu


class ImageFilterFragment : BaseMvpFragment<ImageFilterView, ImageFilterPresenter>(), ImageFilterView {

    companion object {

        const val REQUEST_KEY = "REQUEST_KEY_IMAGE"

        const val BUNDLE_KEY_IMAGE_URI = "BUNDLE_KEY_IMAGE_URI"

        fun newInstance(uri: Uri?): ImageFilterFragment {
            return ImageFilterFragment().apply {
                arguments = bundleOf(
                    BUNDLE_KEY_IMAGE_URI to uri
                )
            }
        }
    }

    override val presenter: ImageFilterPresenter by lazy { Scope.IMAGE_FILTER.get() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.module_image_filter_fragment_filter, container, false)
    }

    private val btnDone by lazy {
        BottomMenu(iconResId = ua.com.compose.R.drawable.ic_done) {
            presenter.pressDone()
        }
    }

    private val btnCancel by lazy {
        BottomMenu(iconResId = ua.com.compose.R.drawable.ic_back) {
           presenter.pressCancelFilter()
        }
    }

    private val btnSettingsRestore by lazy {
        BottomMenu(iconResId = ua.com.compose.R.drawable.ic_settings_restore) {
            list.adapter?.notifyDataSetChanged()
        }
    }

    private val btnPreview by lazy {
        BottomMenu(iconResId = ua.com.compose.R.drawable.ic_eye) {
            list.isVisible = false
            if(presenter.historyFilters.isNotEmpty()){
                (activity as BaseMvpActivity<*, *>).setupBottomMenu(mutableListOf(btnHistory, btnFilters, btnDone))
            }else{
                (activity as BaseMvpActivity<*, *>).setupBottomMenu(mutableListOf(btnFilters, btnDone))
            }
        }
    }

    private val btnFilters by lazy {
        BottomMenu(iconResId = R.drawable.module_image_filter_ic_tune) {
            list.isVisible = true
            presenter.pressMenuFilters()
        }
    }

    private val btnHistory by lazy {
        BottomMenu(iconResId = R.drawable.module_image_filter_ic_history) {
            list.isVisible = true
            presenter.pressMenuHistory()
        }
    }

    override fun initFilter(filter: ImageFilter){
        setTitle(title = "")
        setVisibleBack(isVisible = false)
        initFilterList()
        (activity as BaseMvpActivity<*, *>).setupBottomMenu(mutableListOf(btnCancel, btnSettingsRestore, btnDone))
    }

    override fun initHistory() {
        setVisibleBack(isVisible = true)
        initHistoryList()
        (activity as BaseMvpActivity<*, *>).setupBottomMenu(mutableListOf(btnFilters, btnDone))
    }

    override fun initMenuFilters(){
        initFiltersMenu()
        setTitle(title = requireContext().getString(R.string.module_image_filter_title))
        setVisibleBack(isVisible = true)
        if(presenter.historyFilters.isNotEmpty()){
            (activity as BaseMvpActivity<*, *>).setupBottomMenu(mutableListOf(btnHistory, btnPreview, btnDone))
        }else{
            (activity as BaseMvpActivity<*, *>).setupBottomMenu(mutableListOf(btnPreview, btnDone))
        }
    }

    override fun initBottomMenu() {
        list.layoutManager = null
        list.adapter = null
        setTitle(title = requireContext().getString(R.string.module_image_filter_title))
        if(presenter.historyFilters.isNotEmpty()){
            (activity as BaseMvpActivity<*, *>).setupBottomMenu(mutableListOf(btnFilters, btnHistory, btnPreview, btnDone))
        }else{
            (activity as BaseMvpActivity<*, *>).setupBottomMenu(mutableListOf(btnFilters, btnPreview, btnDone))
        }
    }

    override fun createBottomMenu(): MutableList<Menu> {
        return mutableListOf<Menu>().apply {
            this.add(btnDone)
        }
    }

    private fun initFilterList() {
        list.layoutManager = LinearLayoutManager(getCurrentContext(), RecyclerView.VERTICAL, false)
        list.adapter = ImageFilterRvAdapter(params = presenter.params){
            presenter.onProgressChange()
        }
    }

    private fun initHistoryList() {
        presenter.pressHistory()
        presenter.sampleImage?.let { bm ->
            list.layoutManager = LinearLayoutManager(getCurrentContext(), RecyclerView.HORIZONTAL, false)
            list.adapter = ImageFilterHistoryRvAdapter(requireContext(), bm, presenter.historyFilters.toMutableList()){
                presenter.pressImageHistory(it)
            }
        }
    }

    private fun initFiltersMenu() {
        list.layoutManager = GridLayoutManager(context, 5, RecyclerView.VERTICAL, false)
        list.adapter = ImageFilterMenuRvAdapter(filters = presenter.filters) {
            presenter.pressFilter(it.id)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getCurrentContext().getString(R.string.module_image_filter_title))
        imgView.setZOrderOnTop(true)
        presenter.gpuSampleFilter.setGLSurfaceView(imgView)
        setFragmentResultListener(FragmentGallery.REQUEST_KEY) { _, bundle ->
            val uris = (bundle.getSerializable(FragmentGallery.BUNDLE_KEY_IMAGES) as List<*>).filterIsInstance<Uri>()
            presenter.onAddImage(uris)
        }

        imgView.setOnTouchListener { _, event ->
            when(event.action){
                MotionEvent.ACTION_DOWN -> {
                    presenter.pressImageDown()
                }
                MotionEvent.ACTION_UP -> {
                    presenter.pressImageUp()
                }
            }
            true
        }

        val inputUri = (arguments?.getParcelable(BUNDLE_KEY_IMAGE_URI) as? Uri)

        presenter.onCreate(uri = inputUri)
    }

    override fun vibrateToShowHistory() {
        requireContext().vibrate(EVibrate.BUTTON_LONG)
    }

    override fun updateList() {
        list.adapter?.notifyDataSetChanged()
    }

    override fun backPress(byBack: Boolean): Boolean {
        presenter.pressBack(byBack)
        return true
    }

    override fun openGallery() {
        FragmentGallery.show(fm = getCurrentActivity().supportFragmentManager, isMultiSelect = false)
    }

    override fun setVisibleContent(isVisible: Boolean) {
        container.isInvisible = !isVisible
        imgView.isInvisible = !isVisible
    }

    override fun saveToResult(uri: Uri) {
        setFragmentResult(REQUEST_KEY, bundleOf(BUNDLE_KEY_IMAGE_URI to uri))
    }

    override fun setImage(uri: Uri) {
        Glide.with(this).asBitmap()
            .load(uri)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(object : CustomViewTarget<GLSurfaceView, Bitmap>(imgView) {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    presenter.onSampleLoad(resource)
                    container.isInvisible = false
                    imgView.isInvisible = false
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                }

                override fun onResourceCleared(placeholder: Drawable?) {
                }
            })


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

    override fun backToMain() {
        if(presenter.historyFilters.isNotEmpty() || presenter.currentFilter != null){
            val request = DialogConfirmation.show(fm = requireActivity().supportFragmentManager, message = requireContext().getString(R.string.module_image_filter_exit_without_save))
            setFragmentResultListener(request) { _, bundle ->
                if(bundle.getBoolean(DialogConfirmation.BUNDLE_KEY_ANSWER)){
                    super.backToMain()
                }
            }
        }else{
            super.backToMain()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Scope.IMAGE_FILTER.close()
    }
}