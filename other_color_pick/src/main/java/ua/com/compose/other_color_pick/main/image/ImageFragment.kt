package ua.com.compose.other_color_pick.main.image

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.ColorStateList
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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.Companion.isPhotoPickerAvailable
import androidx.annotation.RequiresApi
import androidx.core.graphics.ColorUtils
import androidx.core.os.bundleOf
import androidx.core.view.drawToBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
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
import ua.com.compose.mvp.data.viewBindingWithBinder
import ua.com.compose.other_color_pick.main.ColorPickViewModule
import ua.com.compose.other_color_pick.main.info.ColorInfoFragment


class ImageFragment : BaseMvvmFragment(R.layout.module_other_color_pick_fragment_image) {

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

    private val btnPaletteAdd = BottomMenu(iconResId = R.drawable.ic_add_circle) {
        viewModule.pressPaletteAdd()
        showAlert(R.string.module_other_color_pick_color_add_to_pallete)
    }

    private val btnCopy = BottomMenu(iconResId = R.drawable.ic_copy) {
        binding.textView.text?.toString()?.let { color ->
            requireContext().clipboardCopy(color)
            showAlert(R.string.module_other_color_pick_color_copy)
        }
    }

    private fun openGallery() {
        if(isPhotoPickerAvailable(requireContext())) {
            pickMedia?.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            FragmentGallery.show(fm = childFragmentManager, isMultiSelect = false)
        }
    }

    override fun createBottomMenu(): MutableList<Menu> {
        return mutableListOf<Menu>()
    }

    private val binding by viewBindingWithBinder(ModuleOtherColorPickFragmentImageBinding::bind)

    private val viewModule: ImageViewModule by lazy {
        Scope.COLOR_PICK.get()
    }

    private val mainModule: ColorPickViewModule by activityViewModels()
    private var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(requireContext().getString(R.string.module_other_color_pick_fragment_title))

        binding.zoomableImageView2.setOnTouchListener { v, event ->
            binding.zoomableImageView2.let { view ->
                detectColor(view)
            }
            binding.zoomableImageView2.onTouch(view, event)
            true
        }

        binding.placeholder.setVibrate(EVibrate.BUTTON)
        binding.placeholder.setOnClickListener {
            openGallery()
        }

        mainModule.colorType.nonNull().observe(viewLifecycleOwner) { type ->
            binding.pointerRing.isVisible.takeIf { it }?.let {
                (activity as BaseMvpActivity<*, *>).setupBottomMenu(mutableListOf(btnGallery, btnCopy, btnPaletteAdd))
            }
            viewModule.updateColor()
        }

        viewModule.nameColor.nonNull().observe(viewLifecycleOwner) { name ->
            binding.txtName.text = name
        }

        viewModule.changeColor.nonNull().observe(viewLifecycleOwner) { color ->
            binding.textView.text = mainModule.colorType.value?.convertColor(color, withSeparator = ",") ?: ""
            binding.imgInfo.imageTintList = ColorStateList.valueOf(if (isDark(color)) Color.WHITE else Color.BLACK)
            binding.txtName.setTextColor( if (isDark(color)) Color.WHITE else Color.BLACK)
            binding.textView.setTextColor( if (isDark(color)) Color.WHITE else Color.BLACK)
            binding.cardView.setCardBackgroundColor(color)
            binding.pointerRing.background.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
            binding.activityMainPointer.background.setColorFilter(if (isDark(color)) Color.WHITE else Color.BLACK, PorterDuff.Mode.SRC_ATOP)
            binding.pointerRing2.background.setColorFilter(if (isDark(color)) Color.WHITE else Color.BLACK, PorterDuff.Mode.SRC_ATOP)
        }

        binding.cardView.setOnClickListener {
            ColorInfoFragment.show(childFragmentManager, color = viewModule.color)
        }

        ((arguments?.getParcelable(BUNDLE_KEY_IMAGE_URI) as? Uri) ?: viewModule.imageUri.uri)?.let { uri ->
            showImage(uri)
        }

        if(isPhotoPickerAvailable(requireContext())) {
            pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    viewModule.imageUri.uri = uri
                    arguments?.putAll(bundleOf(BUNDLE_KEY_IMAGE_URI to uri))
                    showImage(uri)
                }
            }
        } else {
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
        binding.pointerRing.isVisible = true
        binding.pointerRing2.isVisible = true
        binding.activityMainPointer.isVisible = true
        binding.placeholder.isVisible = false
        binding.zoomableImageView2.setBackgroundResource(R.drawable.ic_background)
        binding.cardView.setMarginBottom(requireContext().navigationBarHeight() + 55.dp.toInt() + 8.dp.toInt())
        binding.cardView.isVisible = true
        (activity as BaseMvpActivity<*, *>).setupBottomMenu(mutableListOf(btnGallery, btnCopy, btnPaletteAdd))
        binding.zoomableImageView2.let {
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

