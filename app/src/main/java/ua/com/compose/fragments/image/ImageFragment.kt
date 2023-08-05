package ua.com.compose.fragments.image

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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import org.koin.android.ext.android.get
import org.koin.androidx.scope.requireScopeActivity
import ua.com.compose.MainActivity
import ua.com.compose.R
import ua.com.compose.Settings
import ua.com.compose.api.analytics.Analytics
import ua.com.compose.api.analytics.SimpleEvent
import ua.com.compose.api.analytics.analytics
import ua.com.compose.databinding.ModuleOtherColorPickFragmentImageBinding
import ua.com.compose.extension.*
import ua.com.compose.fragments.ColorPickViewModule
import ua.com.compose.fragments.gallery.FragmentGallery
import ua.com.compose.fragments.info.ColorInfoFragment
import ua.com.compose.mvp.BaseMvpView
import ua.com.compose.mvp.BaseMvvmFragment
import ua.com.compose.mvp.data.BottomMenu
import ua.com.compose.mvp.data.Menu
import ua.com.compose.mvp.data.viewBindingWithBinder


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

    private val btnGallery = BottomMenu(iconResId = ua.com.compose.theme.R.drawable.ic_gallery) {
        openGallery()
    }

    private val btnPaletteAdd = BottomMenu(iconResId = R.drawable.ic_add_circle) {
        viewModule.pressPaletteAdd()
        requireActivity().createReview()
        requireContext().showToast(R.string.module_other_color_pick_color_add_to_pallete)
    }

    private val btnCopy = BottomMenu(iconResId = R.drawable.ic_copy) {
        binding.textView.text?.toString()?.let { color ->
            analytics.send(SimpleEvent(key = Analytics.Event.COLOR_COPY_IMAGE))
            requireContext().clipboardCopy(color)
            requireContext().showToast(R.string.module_other_color_pick_color_copy)
        }
    }

    private var throttleLatestColor: ((Unit) -> Unit)? = null

    private fun openGallery() {
        if(isPhotoPickerAvailable(requireContext())) {
            analytics.send(SimpleEvent(key = Analytics.Event.OPEN_NEW_GALLERY))
            pickMedia?.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            analytics.send(SimpleEvent(key = Analytics.Event.OPEN_OLD_GALLERY))
            FragmentGallery.show(fm = childFragmentManager, isMultiSelect = false)
        }
    }

    override fun createBottomMenu(): MutableList<Menu> {
        return mutableListOf<Menu>()
    }

    private val binding by viewBindingWithBinder(ModuleOtherColorPickFragmentImageBinding::bind)

    private val viewModule: ImageViewModule by lazy {
        requireScopeActivity<MainActivity>().get()
    }

    private val mainModule: ColorPickViewModule by activityViewModels()
    private var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        throttleLatestColor = throttleLatest(
            100L,
            viewLifecycleOwner.lifecycleScope
        ) {
            detectColor(view = binding.zoomableImageView2)
        }

        binding.zoomableImageView2.setMyListener {
           throttleLatestColor?.invoke(Unit)
        }

        binding.placeholder.setVibrate(EVibrate.BUTTON)
        binding.placeholder.setOnClickListener {
            openGallery()
        }

        mainModule.state.nonNull().observe(viewLifecycleOwner) {
            if(it == ColorPickViewModule.State.UPDATE_SETTINGS) {
                viewModule.updateColor()
            }
        }

        viewModule.nameColor.nonNull().observe(viewLifecycleOwner) { name ->
            binding.txtName.text = name
        }

        viewModule.changeColor.nonNull().observe(viewLifecycleOwner) { color ->
            binding.textView.text = Settings.colorType.convertColor(color, withSeparator = ",") ?: ""
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

    override fun onDestroyView() {
        binding.zoomableImageView2.setMyListener(null)
        throttleLatestColor = null
        super.onDestroyView()
    }

    private fun detectColor(view: ImageView) = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getBitmapFormView(view, requireActivity()) {
                it.getPixel(view.width / 2, view.height / 2).let {
                    viewModule.changeColor(color = it)
                }
            }
        } else {
            view.drawToBitmap().getPixel(view.width / 2, view.height / 2).let {
                    viewModule.changeColor(color = it)
            }
        }
    } catch (e: Exception){

    }

    private fun isDark(color: Int): Boolean {
        return ColorUtils.calculateLuminance(color) < 0.5
    }

    private fun visible() {
        binding.pointerRing.isVisible = true
        binding.pointerRing2.isVisible = true
        binding.activityMainPointer.isVisible = true
        binding.placeholder.isVisible = false
        binding.zoomableImageView2.isVisible = true
        binding.zoomableImageContainer.isVisible = true
        binding.zoomableImageContainer.setBackgroundResource(R.drawable.ic_background)
        binding.cardView.setMarginBottom(requireContext().navigationBarHeight() + 55.dp.toInt() + 8.dp.toInt())
        binding.cardView.isVisible = true
        (activity as BaseMvpView).setupBottomMenu(mutableListOf(btnGallery, btnCopy, btnPaletteAdd))
    }

    private fun invisible() {
        binding.pointerRing.isVisible = false
        binding.pointerRing2.isVisible = false
        binding.activityMainPointer.isVisible = false
        binding.placeholder.isVisible = true
        binding.zoomableImageView2.isVisible = false
        binding.zoomableImageContainer.isVisible = false
        binding.cardView.isVisible = false
        (activity as BaseMvpView).setupBottomMenu(mutableListOf())
    }

    private fun showImage(uri: Uri) {
        try {
            visible()
            Glide.with(requireContext())
                .asBitmap()
                .load(uri)
                .centerInside()
                .thumbnail(0.1f)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.zoomableImageView2)
        } catch (e: Exception) {
            viewModule.imageUri.uri = null
            invisible()
        }

    }
}

