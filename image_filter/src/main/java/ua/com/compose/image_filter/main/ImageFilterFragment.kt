package ua.com.compose.image_filter.main

import android.annotation.SuppressLint
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
import androidx.core.view.isVisible
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
import kotlinx.android.synthetic.main.module_image_filter_fragment_filter.*
import ua.com.compose.dialog.dialogs.DialogInput
import ua.com.compose.image_filter.di.Scope
import ua.com.compose.mvp.BaseMvpFragment
import ua.com.compose.gallery.main.FragmentGallery
import ua.com.compose.image_filter.R
import ua.com.compose.image_filter.data.ImageFilter
import ua.com.compose.image_filter.main.dialogFilters.DialogFilters
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
        BottomMenu(iconResId = ua.com.compose.R.drawable.ic_close) {
           presenter.pressCancelFilter()
        }
    }

    private val btnSettingsRestore by lazy {
        BottomMenu(iconResId = ua.com.compose.R.drawable.ic_settings_restore) {
            list.adapter?.notifyDataSetChanged()
        }
    }

    private val btnFilters by lazy {
        BottomMenu(iconResId = R.drawable.module_image_filter_ic_tune) {
            presenter.pressMenuFilters()
        }
    }

//    private val btnStyleAdd by lazy {
//        BottomMenu(iconResId = R.drawable.module_image_filter_ic_style_add) {
//            val request = DialogInput.show(fm = getCurrentActivity().supportFragmentManager, text = requireContext().getString(R.string.module_image_filter_input_style_name), singleLine = true)
//            setFragmentResultListener(request) { _, bundle ->
//                presenter.onInputStyleName(bundle.getString(DialogInput.BUNDLE_KEY_INPUT_MESSAGE))
//            }
//        }.apply {
//            isVisible = { presenter.historyFilters.size > 1 }
//        }
//    }

    private val btnHistory by lazy {
        BottomMenu(iconResId = R.drawable.module_image_filter_ic_history) {
            initHistory()
        }
    }

    override fun initFilter(filter: ImageFilter){
        setTitle(title = requireContext().getString(filter.nameResId))
        setVisibleBack(isVisible = false)
        initFilterList()
        (activity as BaseMvpActivity<*, *>).setupBottomMenu(mutableListOf(btnCancel, btnSettingsRestore, btnDone))
    }

    override fun initHistory() {
        setVisibleBack(isVisible = true)
        setTitle(title = requireContext().getString(R.string.module_image_filter_title))
        initHistoryList()
        (activity as BaseMvpActivity<*, *>).setupBottomMenu(mutableListOf(btnFilters, btnDone))
    }

    override fun initMenuFilters(){
        initFiltersMenu()
        setTitle(title = requireContext().getString(R.string.module_image_filter_title))
        setVisibleBack(isVisible = true)
        (activity as BaseMvpActivity<*, *>).setupBottomMenu(mutableListOf(btnHistory, btnDone))
    }

    override fun createBottomMenu(): MutableList<Menu> {
        return mutableListOf<Menu>().apply {
            this.add(btnHistory)
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
        list.layoutManager = LinearLayoutManager(getCurrentContext(), RecyclerView.HORIZONTAL, false)
        list.adapter = ImageFilterHistoryRvAdapter(presenter.historyImages.reversed().toMutableList(), presenter.historyFilters.reversed().toMutableList()){
            presenter.pressImageHistory(it)
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
        list.adapter?.notifyDataSetChanged()
    }

    override fun backPress(): Boolean {
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

        container.isVisible = true
    }

    override fun setImage(bmp: Bitmap) {
        imgView.setImageBitmap(bmp)
        container.isVisible = true
    }

    override fun onDestroy() {
        super.onDestroy()
        Scope.IMAGE_FILTER.close()
    }
}