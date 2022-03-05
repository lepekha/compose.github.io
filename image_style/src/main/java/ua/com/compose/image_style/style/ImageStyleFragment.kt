package ua.com.compose.image_style.style

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanQRCode
import io.github.g00fy2.quickie.config.ScannerConfig
import kotlinx.android.synthetic.main.module_image_filter_fragment_style.*
import ua.com.compose.dialog.dialogs.DialogConfirmation
import ua.com.compose.dialog.dialogs.DialogImage
import ua.com.compose.dialog.dialogs.DialogInput
import ua.com.compose.mvp.BaseMvpFragment
import ua.com.compose.gallery.main.FragmentGallery
import ua.com.compose.image_style.R
import ua.com.compose.image_style.di.Scope
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

    private val scanQrCode = registerForActivityResult(ScanQRCode(), ::handleResult)
    private val btnQRScanner by lazy {
        BottomMenu(iconResId =  ua.com.compose.image_style.R.drawable.module_image_style_ic_qr_scanner) {
            scanQrCode.launch(null)
        }
    }

    private fun handleResult(result: QRResult) {
        if(result is QRResult.QRSuccess){
            val text = result.content.rawValue
            presenter.createStyleFromQR(text)
        }
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
            this.add(btnQRScanner)
            this.add(btnDone)
        }
    }

    private val itemTouchHelper by lazy {
        val itemTouchCallback = object: ItemTouchHelper.SimpleCallback(UP or DOWN or START or END, 0) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val recyclerviewAdapter = recyclerView.adapter as ImageStyleRvAdapter
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition
                presenter.moveStyle(fromPosition, toPosition)
                recyclerviewAdapter.notifyItemMoved(fromPosition,toPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

        }
        ItemTouchHelper(itemTouchCallback)
    }

    override fun initStyles(image: Bitmap) {
        list.layoutManager = LinearLayoutManager(getCurrentContext(), RecyclerView.HORIZONTAL, false)
        itemTouchHelper.attachToRecyclerView(list)
        list.adapter = ImageStyleRvAdapter(
            requireContext(),
            image,
            presenter.styles,
            onImagePress = {
                presenter.pressStyle(it)
            },
            onRemovePress = {
               presenter.pressRemove(it)
            },
            onQRPress = {
                presenter.generateQRCode(it)
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getCurrentContext().getString(R.string.module_image_style_title))

        imageView.setZOrderOnTop(true)
        presenter.gpuFilter.setGLSurfaceView(imageView)

        imageView.setOnTouchListener { _, event ->
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

        childFragmentManager.setFragmentResultListener(FragmentGallery.REQUEST_KEY, viewLifecycleOwner) { _, bundle ->
            val uris = (bundle.getSerializable(FragmentGallery.BUNDLE_KEY_IMAGES) as List<*>).filterIsInstance<Uri>()
            presenter.onAddImage(uris)
        }

        val inputUri = (arguments?.getParcelable(ImageStyleFragment.BUNDLE_KEY_IMAGE_URI) as? Uri)

        presenter.onCreate(uri = inputUri)
    }

    override fun removeStyle(position: Int) {
        (list.adapter as? ImageStyleRvAdapter)?.remove(position)
    }

    override fun createAddStyleConfirmation(name: String) {
        val request = DialogConfirmation.show(fm = this.childFragmentManager, message = name)
        this.childFragmentManager.setFragmentResultListener(request, viewLifecycleOwner) { _, bundle ->
            if(bundle.getBoolean(DialogConfirmation.BUNDLE_KEY_ANSWER)){
                presenter.onAddStyle()
            }
        }
    }

    override fun backPress(byBack: Boolean): Boolean {
        presenter.pressBack()
        return true
    }

    override fun openGallery() {
        FragmentGallery.show(fm = childFragmentManager, isMultiSelect = false)
    }

    override fun listAddStyle() {
        list.adapter?.notifyItemInserted((list.adapter?.itemCount ?: 1) - 1)
    }

    override fun saveToResult(uri: Uri) {
        setFragmentResult(REQUEST_KEY, bundleOf(BUNDLE_KEY_IMAGE_URI to uri))
    }

    override fun showQRImage(qr: Uri) {
        DialogImage.show(fm = this.childFragmentManager, uri = qr, message = requireContext().getString(R.string.module_image_style_share))
    }

    override fun createRemoveConfirmation() {
        val request = DialogConfirmation.show(fm = this.childFragmentManager, message = requireContext().getString(R.string.module_image_style_remove))
        this.childFragmentManager.setFragmentResultListener(request, viewLifecycleOwner) { _, bundle ->
            if(bundle.getBoolean(DialogConfirmation.BUNDLE_KEY_ANSWER)){
                presenter.onRemove()
            }
        }
    }

    override fun setImage(uri: Uri) {
        Glide.with(requireContext().applicationContext)
                .asBitmap()
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(object: CustomTarget<Bitmap>() {
                    override fun onLoadCleared(placeholder: Drawable?) {}
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        presenter.onResourceLoad(resource)
                        container.isInvisible = false
                        imageView.isInvisible = false
                    }
                })
    }

    override fun onDestroy() {
        super.onDestroy()
        Scope.IMAGE_STYLE.close()
    }
}