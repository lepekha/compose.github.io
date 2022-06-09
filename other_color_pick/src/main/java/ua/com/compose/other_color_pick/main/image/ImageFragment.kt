package ua.com.compose.other_color_pick.main.image

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.graphics.ColorUtils
import androidx.core.os.bundleOf
import androidx.core.view.drawToBitmap
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.com.compose.extension.*
import ua.com.compose.gallery.main.FragmentGallery
import ua.com.compose.mvp.BaseMvpActivity
import ua.com.compose.mvp.BaseMvvmFragment
import ua.com.compose.mvp.data.BottomMenu
import ua.com.compose.mvp.data.Menu
import ua.com.compose.other_color_pick.R
import ua.com.compose.other_color_pick.databinding.ModuleOtherColorPickFragmentImageBinding
import ua.com.compose.other_color_pick.di.Scope


class ImageFragment : BaseMvvmFragment() {

    companion object {

        private const val BUNDLE_KEY_IMAGE_URI = "BUNDLE_KEY_IMAGE_URI"

        fun newInstance(uri: Uri?): ImageFragment {
            return ImageFragment().apply {
                arguments = bundleOf(
                    BUNDLE_KEY_IMAGE_URI to uri
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getBitmapFormView(view: View, activity: Activity, result: (bmp: Bitmap) -> Unit) {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val locations = IntArray(2)
        view.getLocationInWindow(locations)
        val rect =
            Rect(locations[0], locations[1], locations[0] + view.width, locations[1] + view.height)
        PixelCopy.request(activity.getWindow(), rect, bitmap, { copyResult ->
            if (copyResult == PixelCopy.SUCCESS) {
                result(bitmap)
            }
        }, Handler(Looper.getMainLooper()))
    }

    private val btnGallery = BottomMenu(iconResId = ua.com.compose.R.drawable.ic_gallery) {
        openGallery()
    }

    private val btnPaletteAdd = BottomMenu(iconResId = R.drawable.ic_palette_add) {
        viewModule.pressPaletteAdd()
        showAlert(R.string.module_other_color_pick_color_add_to_pallete)
    }

    private val btnSwitch = BottomMenu(iconResId = R.drawable.ic_format_color) {
        viewModule.changeColorType()
    }

    private fun openGallery() {
        FragmentGallery.show(fm = childFragmentManager, isMultiSelect = false)
    }

    override fun createBottomMenu(): MutableList<Menu> {
        return mutableListOf<Menu>().apply {
            this.add(btnGallery)
        }
    }

    private var binding: ModuleOtherColorPickFragmentImageBinding? = null

    private val viewModule: ImageViewModule by lazy {
        Scope.COLOR_PICK.get()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ModuleOtherColorPickFragmentImageBinding.inflate(inflater)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(requireContext().getString(R.string.module_other_color_pick_fragment_title))
        setVisibleBack(true)

        binding?.zoomableImageView2?.setOnTouchListener { v, event ->
            binding?.zoomableImageView2?.let { view ->
                detectColor(view)
            }
            binding?.zoomableImageView2?.onTouch(view, event)
            true
        }

        binding?.placeholder?.setVibrate(EVibrate.BUTTON)
        binding?.placeholder?.setOnClickListener {
            openGallery()
        }

        childFragmentManager.setFragmentResultListener(
            FragmentGallery.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            (bundle.getSerializable(FragmentGallery.BUNDLE_KEY_IMAGES) as List<*>).filterIsInstance<Uri>().firstOrNull()?.let { uri ->
                viewModule.imageUri.uri = uri
                arguments?.putAll(bundleOf(BUNDLE_KEY_IMAGE_URI to uri))
                showImage(uri)
            }
        }

        viewModule.changeColor.nonNull().observe(this) { color ->
            color?.let {
                binding?.textView?.text = color.second
                binding?.textView?.setTextColor( if (isDark(color.first)) Color.WHITE else Color.BLACK)
                binding?.cardView?.setCardBackgroundColor(color.first)
                binding?.pointerRing?.background?.setColorFilter(
                    color.first,
                    PorterDuff.Mode.SRC_ATOP
                )
                binding?.activityMainPointer?.background?.setColorFilter(
                    if (isDark(color.first)) Color.WHITE else Color.BLACK,
                    PorterDuff.Mode.SRC_ATOP
                )
            }
        }

        binding?.btnCopy?.setOnClickListener {
            binding?.textView?.text?.toString()?.let { color ->
                requireContext().clipboardCopy(color)
                showAlert(R.string.module_other_color_pick_color_copy)
            }
        }

        ((arguments?.getParcelable(BUNDLE_KEY_IMAGE_URI) as? Uri) ?: viewModule.imageUri.uri)?.let { uri ->
            showImage(uri)
        }
    }

    private fun detectColor(view: ImageView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getBitmapFormView(view, requireActivity()) {
                it.getPixel(view.width / 2, view.height / 2).let {
                    viewModule.changeColor(color = it)
                }
            }
        } else {
            view.drawToBitmap().getPixel(view.width / 2, view.height / 2).let {
                try {
                    viewModule.changeColor(color = it)
                } catch (e: Exception){ }
            }
        }
    }

    private fun isDark(color: Int): Boolean {
        return ColorUtils.calculateLuminance(color) < 0.5
    }

    private fun showImage(uri: Uri) {
        binding?.pointerRing?.isVisible = true
        binding?.activityMainPointer?.isVisible = true
        binding?.placeholder?.isVisible = false
        binding?.frameLayout?.isVisible = true
        (activity as BaseMvpActivity<*, *>).setupBottomMenu(mutableListOf(btnGallery, btnSwitch, btnPaletteAdd))
        binding?.zoomableImageView2?.let {
            viewLifecycleOwner.lifecycleScope.launch {
                val image = requireContext().loadImage(uri)
                withContext(Dispatchers.Main) {
                    it.setImageBitmap(image)
                    it.post {
                        detectColor(it)
                    }
                }
            }
        }
    }
}

